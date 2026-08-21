import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Rol } from '../models/interfaces/Rol.interface';

@Injectable({
  providedIn: 'root',
})
export class RolService {
  private readonly url = 'http://localhost:8080/api/rol';

  constructor(private http: HttpClient) {}

  getRoles(): Observable<Rol[]> {
    return this.http.get<Rol[]>(this.url);
  }
}
