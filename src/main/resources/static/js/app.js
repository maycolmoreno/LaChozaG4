// JS global para La Choza

(function() {
    'use strict';

    // === TOAST GLOBAL ===
    window.showToast = function(message, type) {
        type = type || 'success';
        var container = document.getElementById('toast-container');
        if (!container) return;

        var iconMap = {
            'success': 'bi-check-circle-fill',
            'danger':  'bi-x-circle-fill',
            'warning': 'bi-exclamation-triangle-fill',
            'info':    'bi-info-circle-fill'
        };
        var colorMap = {
            'success': '#7DBE31',
            'danger':  '#E74C3C',
            'warning': '#f59e0b',
            'info':    '#6366f1'
        };

        var icon = iconMap[type] || iconMap['info'];
        var color = colorMap[type] || colorMap['info'];

        var id = 'toast-' + Date.now();
        var html = '<div id="' + id + '" class="toast align-items-center border-0 shadow-lg show" role="alert" ' +
            'style="min-width:320px;border-left:4px solid ' + color + ';">' +
            '<div class="d-flex">' +
            '<div class="toast-body d-flex align-items-center gap-2">' +
            '<i class="bi ' + icon + ' fs-5" style="color:' + color + ';"></i>' +
            '<span>' + message + '</span>' +
            '</div>' +
            '<button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast"></button>' +
            '</div></div>';

        container.insertAdjacentHTML('beforeend', html);

        // Auto dismiss after 4s
        setTimeout(function() {
            var el = document.getElementById(id);
            if (el) {
                el.style.transition = 'opacity .4s, transform .4s';
                el.style.opacity = '0';
                el.style.transform = 'translateX(30px)';
                setTimeout(function(){ el.remove(); }, 400);
            }
        }, 4000);
    };

    document.addEventListener('DOMContentLoaded', function() {
        initGlobalSearch();
        initFormValidation();
        initClienteNumericInputs();
        initCocinaAutoRefresh();
        initProductoCatalogo();
        initEntregaNotificaciones();
    });

    function initGlobalSearch() {
        var input = document.getElementById('global-search');
        if (!input) return;

        input.addEventListener('keyup', function() {
            var value = input.value.toLowerCase();
            var rows = document.querySelectorAll('.table tbody tr');
            rows.forEach(function(row) {
                var text = row.textContent.toLowerCase();
                row.style.display = text.indexOf(value) > -1 ? '' : 'none';
            });
        });
    }

    function initFormValidation() {
        var forms = document.querySelectorAll('.needs-validation');
        if (!forms || forms.length === 0) return;

        Array.prototype.slice.call(forms).forEach(function(form) {
            form.addEventListener('submit', function(event) {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                    form.classList.add('was-validated');
                    var firstInvalid = form.querySelector(':invalid');
                    if (firstInvalid) {
                        firstInvalid.focus();
                    }
                }
            }, false);
        });
    }

    function initClienteNumericInputs() {
        var ids = ['cedula', 'telefono'];
        ids.forEach(function(id) {
            var input = document.getElementById(id);
            if (!input) return;
            input.addEventListener('input', function() {
                this.value = this.value.replace(/[^0-9]/g, '');
            });
        });
    }

    function initCocinaAutoRefresh() {
        var countdownEl = document.getElementById('countdown');
        if (!countdownEl) return;

        var countdown = parseInt(countdownEl.textContent || '30', 10);
        if (isNaN(countdown) || countdown <= 0) {
            countdown = 30;
            countdownEl.textContent = countdown;
        }

        setInterval(function() {
            countdown--;
            countdownEl.textContent = countdown;
            if (countdown <= 0) {
                location.reload();
            }
        }, 1000);
    }

    function initProductoCatalogo() {
        var productosGrid = document.getElementById('productosGrid');
        if (!productosGrid) return;

        var filtroCategoria = 'todos';
        var filtroEstado = 'activos';

        var botonesCategoria = document.querySelectorAll('.btn-categoria');
        botonesCategoria.forEach(function(btn) {
            btn.addEventListener('click', function(e) {
                e.preventDefault();

                botonesCategoria.forEach(function(b) { b.classList.remove('active'); });
                this.classList.add('active');

                var categoria = this.dataset.categoria;

                if (categoria === 'todos') {
                    filtroCategoria = 'todos';
                    filtroEstado = 'activos';
                } else if (categoria === 'inactivos') {
                    filtroCategoria = 'todos';
                    filtroEstado = 'inactivos';
                } else {
                    filtroCategoria = categoria;
                    filtroEstado = 'activos';
                }

                filtrarProductos();
            });
        });

        var inputBusqueda = document.getElementById('searchProduct');
        if (inputBusqueda) {
            inputBusqueda.addEventListener('input', filtrarProductos);
            inputBusqueda.addEventListener('keyup', filtrarProductos);
        }

        function filtrarProductos() {
            var searchTerm = '';
            if (inputBusqueda) {
                searchTerm = inputBusqueda.value.toLowerCase().trim();
            }

            var productosVisibles = 0;
            var items = document.querySelectorAll('.producto-item');

            items.forEach(function(item) {
                var card = item.querySelector('.product-card');
                var titleEl = card ? card.querySelector('.product-title') : null;
                var descEl = card ? card.querySelector('.product-description') : null;

                var title = titleEl ? titleEl.textContent.toLowerCase() : '';
                var desc = descEl ? descEl.textContent.toLowerCase() : '';
                var categoriaProducto = item.dataset.categoria;
                var esActivo = item.dataset.estado === 'true';

                var cumpleEstado = true;
                if (filtroEstado === 'activos') {
                    cumpleEstado = esActivo;
                } else if (filtroEstado === 'inactivos') {
                    cumpleEstado = !esActivo;
                }

                var cumpleCategoria = true;
                if (filtroCategoria !== 'todos') {
                    cumpleCategoria = categoriaProducto === filtroCategoria;
                }

                var cumpleBusqueda = true;
                if (searchTerm) {
                    cumpleBusqueda = title.indexOf(searchTerm) > -1 || desc.indexOf(searchTerm) > -1;
                }

                var mostrar = cumpleEstado && cumpleCategoria && cumpleBusqueda;
                item.style.display = mostrar ? '' : 'none';

                if (mostrar) {
                    productosVisibles++;
                }
            });

            var contadorEl = document.getElementById('contadorProductos');
            if (contadorEl) {
                contadorEl.textContent = productosVisibles;
            }
        }
    }

    /**
     * Verifica periódicamente si hay pedidos LISTO_PARA_ENTREGA nuevos.
     * Muestra un toast + sonido de notificación cuando hay pedidos listos que no se han visto.
     * Solo se activa si el elemento #alerta-por-entregar existe en la página o si estamos en /pedidos.
     */
    function initEntregaNotificaciones() {
        // Solo activar en páginas de pedidos (no en cocina)
        var esPaginaPedidos = window.location.pathname.indexOf('/pedidos') > -1
                           || window.location.pathname.indexOf('/dashboard') > -1;
        if (!esPaginaPedidos) return;

        var ultimoConteo = parseInt(sessionStorage.getItem('listoParaEntregaCount') || '0', 10);

        function verificarPedidosListos() {
            fetch('/api/pos/pedidos-listos-count')
                .then(function(resp) {
                    if (!resp.ok) return null;
                    return resp.json();
                })
                .then(function(data) {
                    if (data === null || data === undefined) return;
                    var cantidad = data.count || 0;

                    if (cantidad > ultimoConteo && cantidad > 0) {
                        // Hay nuevos pedidos listos para entrega
                        reproducirSonidoNotificacion();
                        showToast(
                            '<i class="bi bi-bell-fill"></i> ' + cantidad + ' pedido(s) listo(s) para entregar a la mesa',
                            'info'
                        );
                    }

                    ultimoConteo = cantidad;
                    sessionStorage.setItem('listoParaEntregaCount', cantidad.toString());
                })
                .catch(function() {
                    // Silenciar errores de red
                });
        }

        // Verificar cada 15 segundos
        setInterval(verificarPedidosListos, 15000);
        // Primera verificación inmediata (con delay breve para que cargue la página)
        setTimeout(verificarPedidosListos, 2000);
    }

    /**
     * Reproduce un sonido de notificación usando la Web Audio API.
     */
    function reproducirSonidoNotificacion() {
        try {
            var audioCtx = new (window.AudioContext || window.webkitAudioContext)();

            // Primer tono
            var osc1 = audioCtx.createOscillator();
            var gain1 = audioCtx.createGain();
            osc1.connect(gain1);
            gain1.connect(audioCtx.destination);
            osc1.frequency.value = 587.33; // D5
            osc1.type = 'sine';
            gain1.gain.setValueAtTime(0.3, audioCtx.currentTime);
            gain1.gain.exponentialRampToValueAtTime(0.01, audioCtx.currentTime + 0.3);
            osc1.start(audioCtx.currentTime);
            osc1.stop(audioCtx.currentTime + 0.3);

            // Segundo tono (más alto)
            var osc2 = audioCtx.createOscillator();
            var gain2 = audioCtx.createGain();
            osc2.connect(gain2);
            gain2.connect(audioCtx.destination);
            osc2.frequency.value = 880; // A5
            osc2.type = 'sine';
            gain2.gain.setValueAtTime(0.3, audioCtx.currentTime + 0.15);
            gain2.gain.exponentialRampToValueAtTime(0.01, audioCtx.currentTime + 0.5);
            osc2.start(audioCtx.currentTime + 0.15);
            osc2.stop(audioCtx.currentTime + 0.5);
        } catch (e) {
            // Navegador no soporta Web Audio API
        }
    }

})();
