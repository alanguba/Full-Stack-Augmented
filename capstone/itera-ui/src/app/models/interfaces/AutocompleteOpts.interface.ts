import { BoundsLiteral } from "./BoundsLiteral.interface";

export interface AutocompleteOpts {
  includedPrimaryTypes?: string[];
  includedRegionCodes?: string[]; // hasta 15 [2](https://googleapis.dev/dotnet/Google.Maps.Places.V1/latest/api/Google.Maps.Places.V1.AutocompletePlacesRequest.html)
  region?: string;
  language?: string;
  origin?: { lat: number; lng: number };
  locationRestriction?: BoundsLiteral; // west/north/east/south [1](https://developers.google.com/maps/documentation/javascript/place-autocomplete-data)
}
