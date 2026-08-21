import { Component, inject, OnInit } from '@angular/core';
import { UsuarioService } from '../../../services/usuario.service';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { UserDialogComponent } from '../../../components/user-dialog/user-dialog.component';
import { Usuario } from '../../../models/interfaces/Usuario.interface';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'app-usuario',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatCardModule,
    MatChipsModule,
    MatTooltipModule,
  ],
  templateUrl: './usuario.page.html',
  styleUrl: './usuario.page.css',
})
export class UsuarioPage implements OnInit {
  private userService = inject(UsuarioService);
  private dialog = inject(MatDialog);

  users$!: Observable<Usuario[]>;
  displayedColumns: string[] = ['name', 'apellidoPaterno', 'apellidoMaterno', 'email', 'role', 'status', 'actions'];

  ngOnInit(): void {
    this.users$ = this.userService.getUsers();
  }

  loadUsers(): void {
    this.users$ = this.userService.getUsers();
  }

  openCreateDialog(): void {
    const dialogRef = this.dialog.open(UserDialogComponent, {
      width: '500px',
      data: { mode: 'create' },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loadUsers();
      }
    });
  }

  openEditDialog(user: Usuario): void {
    const dialogRef = this.dialog.open(UserDialogComponent, {
      width: '500px',
      data: {
        mode: 'edit',
        usuario: user,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loadUsers();
      }
    });
  }

  toggleStatus(user: Usuario): void {
    user.estado = user.estado === 1 ? 0 : 1;
    this.userService.toggleUserStatus(user).subscribe(() => {
      this.loadUsers();
    });
  }
}
