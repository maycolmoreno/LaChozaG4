/*
 * Service Worker — La Choza PWA
 * Estrategia: Network First (siempre intenta la red, cae a caché si falla).
 * Cachea los assets estáticos para arranque rápido y modo offline básico.
 */

const CACHE_NAME = 'lachoza-v1';

// Assets estáticos que se pre-cachean en la instalación
const PRECACHE_URLS = [
  '/',
  '/login',
  '/css/custom-modern.css',
  '/css/style-login.css',
  '/js/app.js',
  '/assets/images/favicon.svg',
  '/assets/images/logolachoza.jpg',
  '/manifest.json',
  '/offline.html'
];

// Instalar: pre-cachear assets esenciales
self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(cache => cache.addAll(PRECACHE_URLS))
      .then(() => self.skipWaiting())
  );
});

// Activar: limpiar cachés viejos
self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(cacheNames =>
      Promise.all(
        cacheNames
          .filter(name => name !== CACHE_NAME)
          .map(name => caches.delete(name))
      )
    ).then(() => self.clients.claim())
  );
});

// Fetch: Network First → Cache fallback → Offline page
self.addEventListener('fetch', event => {
  // Ignorar requests que no sean GET
  if (event.request.method !== 'GET') return;

  // Ignorar requests a APIs externas (solo cachear nuestro dominio)
  if (!event.request.url.startsWith(self.location.origin)) return;

  event.respondWith(
    fetch(event.request)
      .then(response => {
        // Si la respuesta es válida, guardarla en caché
        if (response.ok) {
          const responseClone = response.clone();
          caches.open(CACHE_NAME).then(cache => {
            cache.put(event.request, responseClone);
          });
        }
        return response;
      })
      .catch(() => {
        // Sin red → buscar en caché
        return caches.match(event.request).then(cached => {
          if (cached) return cached;
          // Si es una navegación (página HTML), mostrar offline.html
          if (event.request.mode === 'navigate') {
            return caches.match('/offline.html');
          }
          return new Response('', { status: 503, statusText: 'Offline' });
        });
      })
  );
});
