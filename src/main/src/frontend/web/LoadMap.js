const germanyBounds = L.latLngBounds([47.270, 5.866], [55.058, 15.042]);

const map = L.map('map', {
    maxBounds: germanyBounds,
    maxBoundsViscosity: 1.0,
    inertia: false
});

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; OpenStreetMap contributors'
}).addTo(map);

map.fitBounds(germanyBounds);

async function loadPoints() {
    const res = await fetch('/api/points');
    const points = await res.json();
    points.forEach(p => L.marker([p.lat, p.lon]).addTo(map).bindPopup(p.label));
}

document.getElementById('btnPoints').addEventListener('click', loadPoints);


setTimeout(() => map.invalidateSize(), 0);
