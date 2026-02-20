const germanyBounds = L.latLngBounds([47.270, 5.866], [55.058, 15.042]);

const slider = document.getElementById('rangeSlider');
const sliderValue = document.getElementById('sliderValue');
var markerAmount = 0;
var markerA = null;
var markerB = null;
var markerAId = null;
var markerBId = null;

slider.addEventListener('input', () => {
    sliderValue.textContent = slider.value;
});


const map = L.map('map', {
    maxBounds: germanyBounds,
    maxBoundsViscosity: 1.0,
    inertia: false
});


map.addEventListener('click', async (e) => {
    if (markerAmount < 2) {
        const lat = e.latlng.lat;
        const lon = e.latlng.lng;
        console.log("Sending coordinates to backend: Lat "+lat+", Lon "+e.latlng.lng);
        const res = await fetch('/api/nearest', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ lat: lat, lon: lon })
        });
        const result = await res.json();
        console.log("Nearest point: Lat "+result.lat+", Lon "+result.lon+", Node id "+result.id);
        const latlng = L.latLng(result.lat, result.lon);
        if (markerAmount == 0) {
            markerAId = result.id;
            const iconA = L.icon({
                iconUrl: "MarkerA.png",
                iconSize: [51, 48],
                iconAnchor: [26, 48]    
            })
            markerA = new L.marker(latlng, {icon: iconA}).addTo(map);
            map.addLayer(markerA);
        }
        if (markerAmount == 1) {
            markerBId = result.id;
            const iconB = L.icon({
                iconUrl: "MarkerB.png",
                iconSize: [51, 48],
                iconAnchor: [26, 48]    
            })
            markerB = new L.marker(latlng, {icon: iconB}).addTo(map);
            map.addLayer(markerB);
        }
        markerAmount++;
    } else {
        console.log("There are already two markers on the map. Reset the markers if you want to place new ones.");
    }
});


L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; OpenStreetMap contributors'
}).addTo(map);

map.fitBounds(germanyBounds);

async function calculateRoute() {
    console.log("Route calculation initialised. Passing values to backend.");
    const sliderWeight = slider.value;
    const aLat = markerA.getLatLng().lat;
    const aLon = markerA.getLatLng().lng;
    const bLat = markerB.getLatLng().lat;
    const bLon = markerB.getLatLng().lng;
    const aId = markerAId;
    const bId = markerBId;

    console.log("sliderWeight: "+sliderWeight+", aLat: "+aLat+", aLon: "+aLon+", aId: "+aId+", bLat: "+bLat+", bLon: "+bLon+", bId: "+bId);
    
    const res = await fetch('/api/route', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ sliderWeight: sliderWeight, aLat: aLat, aLon: aLon, aId: aId, bLat: bLat, bLon: bLon, bId: bId})
        });

    const points = await res.json();
    points.forEach(p => L.marker([p.lat, p.lon]).addTo(map).bindPopup(p.label));
}

async function resetMarkers() {
    markerAmount = 0;
    map.removeLayer(markerA);
    map.removeLayer(markerB);
    markerAId = null;
    markerBId = null;
}


document.getElementById('calculateRoute').addEventListener('click', calculateRoute);
document.getElementById('resetMarkers').addEventListener('click', resetMarkers);

setTimeout(() => map.invalidateSize(), 0);
