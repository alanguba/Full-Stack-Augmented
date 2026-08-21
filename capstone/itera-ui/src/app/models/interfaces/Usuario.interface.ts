import { Rol } from "./Rol.interface";

export interface Usuario {
  id: number;
  nombre: string;
  correo: string;
  apellidoPaterno: string;
  apellidoMaterno: string;
  rol: Rol;
  estado: number; // 1 para activo, 0 para inactivo
}

export interface UsuarioFormValue {
  id: number | null;
  nombre: string;
  correo: string;
  apellidoPaterno: string;
  apellidoMaterno: string;
  rol: Rol | null;
  estado: number;
}
