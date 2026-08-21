import { Injectable } from '@angular/core';
import { AutocompleteOpts } from '../models/interfaces/AutocompleteOpts.interface';
import { NearbyPlaceVM } from '../models/interfaces/NearbyPlaceVM.interface';

export interface PlaceSuggestionVM {
  text: string; // lo que se ve en la lista
  placeId?: string; // si lo quieres guardar explícitamente
  prediction?: any; // guardamos PlacePrediction para convertir a Place
}

@Injectable({ providedIn: 'root' })
export class GooglePlacesService {
  private placesLibPromise?: Promise<google.maps.PlacesLibrary>;
  private token?: google.maps.places.AutocompleteSessionToken;

  /** Espera a que Google Maps JS esté disponible y pide la librería "places". */
  async loadPlacesLibrary(): Promise<google.maps.PlacesLibrary> {
    if (this.placesLibPromise) return this.placesLibPromise;

    this.placesLibPromise = new Promise(async (resolve, reject) => {
      // espera a que el script haya cargado
      const maxWaitMs = 10000;
      const start = Date.now();

      const wait = () => {
        if ((window as any).google?.maps?.importLibrary) {
          (window as any).google.maps
            .importLibrary('places')
            .then(() => resolve((window as any).google.maps as any))
            .catch(reject);
          return;
        }
        if ((window as any).google?.maps?.places) {
          // fallback (si importLibrary no está expuesto por tu loader)
          resolve((window as any).google.maps as any);
          return;
        }
        if (Date.now() - start > maxWaitMs) {
          reject(new Error('Google Maps JS no cargó (timeout).'));
          return;
        }
        setTimeout(wait, 50);
      };

      wait();
    }) as unknown as Promise<google.maps.PlacesLibrary>;

    return this.placesLibPromise;
  }

  /** Crea/renueva token de sesión (recomendado por Google). */
  async newSessionToken(): Promise<google.maps.places.AutocompleteSessionToken> {
    await this.loadPlacesLibrary();
    this.token = new (google.maps.places as any).AutocompleteSessionToken();
    return this.token!;
  }

  /** Obtiene sugerencias (API nuevo). */
  async fetchSuggestions(input: string, opts?: AutocompleteOpts): Promise<PlaceSuggestionVM[]> {
    await this.loadPlacesLibrary();

    if (!this.token) {
      await this.newSessionToken();
    }

    // API nuevo: fetchAutocompleteSuggestions()
    // docs: Place Autocomplete Data API / AutocompleteSuggestion [7](https://developers.google.com/maps/documentation/javascript/place-autocomplete-data)[8](https://developers.google.com/maps/documentation/javascript/reference/autocomplete-data)
    const AutocompleteSuggestion = (google.maps.places as any).AutocompleteSuggestion;

    const request: any = {
      input,
      sessionToken: this.token,
      ...(opts?.includedPrimaryTypes ? { includedPrimaryTypes: opts.includedPrimaryTypes } : {}),
      ...(opts?.includedRegionCodes ? { includedRegionCodes: opts.includedRegionCodes } : {}),
      ...(opts?.language ? { language: opts.language } : {}),
      ...(opts?.region ? { region: opts.region } : {}),
      ...(opts?.origin ? { origin: opts.origin } : {}),
      ...(opts?.locationRestriction ? { locationRestriction: opts.locationRestriction } : {}),
    };

    const { suggestions } = await AutocompleteSuggestion.fetchAutocompleteSuggestions(request);

    return (suggestions ?? [])
      .map((s: any) => s?.placePrediction)
      .filter(Boolean)
      .map((pp: any) => ({
        text: pp.text?.toString?.() ?? pp.structuredFormat?.mainText?.toString?.() ?? '',
        placeId: pp.placeId,
        prediction: pp,
      }));
  }

  /** Convierte PlacePrediction -> Place y trae campos (detalles). */
  async fetchPlaceDetails(prediction: any): Promise<{
    name?: string;
    address?: string;
    lat?: number;
    lng?: number;
    primaryType?: string;
  }> {
    await this.loadPlacesLibrary();

    const place = prediction.toPlace(); // PlacePrediction.toPlace()
    // Con el API nuevo, los campos se solicitan al hacer fetchFields() [2](https://developers.google.com/maps/documentation/javascript/legacy/places-migration-autocomplete)
    await place.fetchFields({
      fields: ['displayName', 'formattedAddress', 'location', 'primaryType'],
    });

    return {
      name: place.displayName,
      address: place.formattedAddress,
      lat: place.location?.lat(),
      lng: place.location?.lng(),
      primaryType: place.primaryType,
    };
  }

  async fetchPlaceDetailsById(placeId: string): Promise<{
    id: string;
    name?: string;
    address?: string;
    lat?: number;
    lng?: number;
    primaryType?: string;
    googleMapsURI?: string;
  }> {
    await this.loadPlacesLibrary();

    // Place Details (New): se crea Place desde placeId y se llama fetchFields() [5](https://developers.google.com/maps/documentation/javascript/place-details)
    const place = new (google.maps.places as any).Place({ id: placeId });

    await place.fetchFields({
      fields: ['id', 'displayName', 'formattedAddress', 'location', 'primaryType', 'googleMapsURI'],
    });

    return {
      id: place.id,
      name: place.displayName,
      address: place.formattedAddress,
      lat: place.location?.lat(),
      lng: place.location?.lng(),
      primaryType: place.primaryType,
      googleMapsURI: place.googleMapsURI,
    };
  }

  async searchNearby(opts: {
    center: { lat: number; lng: number };
    radiusMeters: number;
    includedPrimaryTypes?: string[]; // hasta 5 típicamente en autocomplete; en searchNearby también se usa en el sample [2](https://developers.google.com/maps/documentation/javascript/examples/place-nearby-search)
    maxResultCount?: number;
  }): Promise<NearbyPlaceVM[]> {
    await this.loadPlacesLibrary();

    const { Place } = google.maps.places as any;

    // Place.searchNearby (Nearby Search New) [3](https://developers.google.com/maps/documentation/javascript/reference/place)[2](https://developers.google.com/maps/documentation/javascript/examples/place-nearby-search)
    const request: any = {
      fields: ['id', 'displayName', 'location', 'formattedAddress', 'primaryType'],
      locationRestriction: {
        center: opts.center,
        radius: Math.min(opts.radiusMeters, 50000), // el sample limita el radio con un max [7](https://developers.google.cn/maps/documentation/javascript/examples/place-nearby-search)
      },
      ...(opts.includedPrimaryTypes?.length
        ? { includedPrimaryTypes: opts.includedPrimaryTypes }
        : {}),
      maxResultCount: opts.maxResultCount ?? 8,
    };

    const { places } = await Place.searchNearby(request);

    return (places ?? [])
      .filter((p: any) => p?.location)
      .map((p: any) => ({
        id: p.id,
        name: p.displayName,
        address: p.formattedAddress,
        lat: p.location.lat(),
        lng: p.location.lng(),
        primaryType: p.primaryType,
      }));
  }

  /** Cierra sesión (opcional): crea token nuevo para la siguiente búsqueda grande */
  async endSession(): Promise<void> {
    this.token = undefined;
  }
}
