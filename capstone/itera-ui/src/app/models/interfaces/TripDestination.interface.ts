import { BoundsLiteral } from "./BoundsLiteral.interface";

export interface TripDestination {
  label: string;                 // “Monterrey, NL, México”
  placeId?: string;
  center?: { lat: number; lng: number };
  bounds?: BoundsLiteral;        // locationRestriction para Places [1](https://developers.google.com/maps/documentation/javascript/place-autocomplete-data)
}
