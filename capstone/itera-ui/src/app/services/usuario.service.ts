import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Usuario, UsuarioFormValue } from '../models/interfaces/Usuario.interface';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class UsuarioService {
  private readonly url = 'http://localhost:8080/api/usuario';

  constructor(private http: HttpClient) {}

  getUsers(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.url + '/getAll');
  }

  createUser(user: Omit<UsuarioFormValue, 'id'>): Observable<Usuario> {
    return this.http.post<Usuario>(this.url + '/add', user);
  }

  updateUser(updatedUser: UsuarioFormValue): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.url}/update`, updatedUser);
  }

  toggleUserStatus(updatedUser: Usuario): Observable<Usuario> {
    return this.http.patch<Usuario>(`${this.url}/status`, updatedUser);
  }
}
