const germanyBounds = L.latLngBounds([47.270, 5.866], [55.058, 15.042]);

const slider = document.getElementById('rangeSlider');
const sliderValue = document.getElementById('sliderValue');
var sliderFunctionValue = 0;
var markerAmount = 0;
var markerA = null;
var markerB = null;
var markerAId = null;
var markerBId = null;
var routeLayer = null;

slider.addEventListener('input', () => {
    sliderFunctionValue = Math.pow(slider.value, 8);
    sliderValue.textContent = sliderFunctionValue;
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
        console.log("Sending coordinates to backend to find closest node to: Lat "+lat+", Lon "+e.latlng.lng);
        const res = await fetch('/api/nearest', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ lat: lat, lon: lon })
        });
        const result = await res.json();
        console.log("Backend found closest node at: Lat "+result.lat+", Lon "+result.lon+", Node id "+result.id+". Marker placed.");
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
        console.log("There are already two markers on the map. Reset the markers if you want to place them somewhere else.");
    }
});


L.tileLayer('https://tiles.fmi.uni-stuttgart.de/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; OpenStreetMap contributors'
}).addTo(map);

map.fitBounds(germanyBounds);

async function calculateRoute() {
    if (!markerA || !markerB) {
        console.log("Place two markers first.");
        return;
    }

    const sliderWeight = Number(sliderFunctionValue);
    const aLat = markerA.getLatLng().lat;
    const aLon = markerA.getLatLng().lng;
    const bLat = markerB.getLatLng().lat;
    const bLon = markerB.getLatLng().lng;
    const aId = markerAId;
    const bId = markerBId;

    console.log("Sending marker and slider information to backend. sliderWeight: "+sliderWeight+", aLat: "+aLat+", aLon: "+aLon+", aId: "+aId+", bLat: "+bLat+", bLon: "+bLon+", bId: "+bId);
    
    const res = await fetch('/api/route', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ sliderWeight: sliderWeight, aLat: aLat, aLon: aLon, aId: aId, bLat: bLat, bLon: bLon, bId: bId})
        });

    console.log("Received route information from backend. Displaying route.");

    if (!res.ok) {
        const txt = await res.text();
        console.error("Route request failed:", res.status, txt);
        return;
    }

    const geojson = await res.json();

    if (routeLayer) {
        map.removeLayer(routeLayer);
        routeLayer = null;
        document.getElementById("distance_km").textContent = "-";
        document.getElementById("elevation_m").textContent = "-";
    }

    const line = geojson.features.find(f => f.geometry && f.geometry.type === "LineString");

    if (line && line.properties) {
        const distCm = line.properties.distance_cm ?? null;
        const elevationCm = line.properties.elevation_cm ?? null;

        if (distCm != null) {
            const distKm = distCm / 100000.0;
            document.getElementById("distance_km").textContent = distKm.toFixed(2);
        } else {
            document.getElementById("distance_km").textContent = "-";
        }

    if (elevationCm != null) {
            const elevM = elevationCm / 100.0;
            document.getElementById("elevation_m").textContent = Math.round(elevM).toString();
        } else {
            document.getElementById("elevation_m").textContent = "-";
        }
    }

    routeLayer = L.geoJSON(geojson, {
        style: (feature) => {
            if (feature.geometry && feature.geometry.type === "LineString") {
                return { weight: 6 };
            }
            return {};
        },
        pointToLayer: function (feature, latlng) {
            return null;
        }
    }).addTo(map);

    const bounds = routeLayer.getBounds();
    if (bounds && bounds.isValid()) {
        map.fitBounds(bounds, { padding: [20, 20] });
    }
}

async function resetMarkers() {
    markerAmount = 0;
    if (markerA != null) {
        map.removeLayer(markerA);
        markerAId = null;
        markerA = null;
    }
    if (markerB != null) {
        map.removeLayer(markerB);
        markerBId = null;
        markerB = null;
    }
    if (routeLayer) {
        map.removeLayer(routeLayer);
        routeLayer = null;
        document.getElementById("distance_km").textContent = "-";
        document.getElementById("elevation_m").textContent = "-";
    }
}


document.getElementById('calculateRoute').addEventListener('click', calculateRoute);
document.getElementById('resetMarkers').addEventListener('click', resetMarkers);

setTimeout(() => map.invalidateSize(), 0);
