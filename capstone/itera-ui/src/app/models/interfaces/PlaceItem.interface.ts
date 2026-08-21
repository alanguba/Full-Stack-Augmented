import { PlaceCategory } from "../types/PlaceCategory.type";

export interface PlaceItem {
  id: string;
  name: string;
  address: string;
  category: string;
  rating?: number;
  icon: string;
  lat: number;
  lng: number;
}