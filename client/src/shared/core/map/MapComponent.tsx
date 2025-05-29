import {MapContainer, Marker, TileLayer} from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import React from "react";

interface MapComponentProps {
  position: [number, number];
  style: React.CSSProperties;
}

const MapComponent = ({position, style}: MapComponentProps) => {
  return (
    // @ts-ignore
    <MapContainer center={position} zoom={13} style={style}>
      <TileLayer
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        // @ts-ignore
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
      />
      <Marker position={position}/>
    </MapContainer>
  );
};

export default MapComponent;