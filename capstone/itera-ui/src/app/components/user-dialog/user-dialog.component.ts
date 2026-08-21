import { Component, Inject, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { Rol } from '../../models/interfaces/Rol.interface';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { Usuario } from '../../models/interfaces/Usuario.interface';
import { UsuarioService } from '../../services/usuario.service';
import { RolService } from '../../services/rol.service';
import { ToastService } from '../../services/toast.service';

export interface UserDialogData {
  mode: 'create' | 'edit';
  user?: Usuario;
}

@Component({
  selector: 'app-user-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
  ],
  templateUrl: './user-dialog.component.html',
  styleUrls: ['./user-dialog.component.css'],
})
export class UserDialogComponent implements OnInit {
  private fb = inject(FormBuilder);
  private toastService = inject(ToastService);
  private usuarioService = inject(UsuarioService);
  private rolService = inject(RolService);
  private dialogRef = inject(MatDialogRef<UserDialogComponent>);
  isSaving = false;
  isLoadingRoles = false;

  roles: Rol[] = [];

  form = this.fb.group({
    id: this.fb.control<number | null>(null),
    nombre: this.fb.nonNullable.control('', [Validators.required]),
    correo: this.fb.nonNullable.control('', [Validators.required, Validators.email]),
    apellidoPaterno: this.fb.nonNullable.control('', [Validators.required]),
    apellidoMaterno: this.fb.nonNullable.control(''),
    rol: this.fb.control<Rol | null>(null, [Validators.required]),
    estado: this.fb.nonNullable.control(1),
  });

  constructor(
    @Inject(MAT_DIALOG_DATA) public data?: { mode: 'create' | 'edit'; usuario?: Usuario },
  ) {}

  ngOnInit(): void {
    this.loadRoles();
    if (this.data?.mode === 'edit' && this.data.usuario) {
      this.form.patchValue(this.data.usuario);
      this.form.get('rol')?.setValue(this.data.usuario.rol);
    }
  }

  private loadRoles(): void {
    this.isLoadingRoles = true;
    this.rolService.getRoles().subscribe({
      next: (roles) => {
        this.roles = roles;
        this.isLoadingRoles = false;
        if (this.data?.mode === 'edit' && this.data.usuario) {
          this.form.get('rol')?.setValue(this.data.usuario.rol);
        }
      },
      error: (error) => {
        console.error('Error al cargar roles', error);
        this.isLoadingRoles = false;
      },
    });
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSaving = true;

    const formValue = this.form.getRawValue();

    const request$ = this.data?.usuario?.id
      ? this.usuarioService.updateUser({
          ...formValue,
          id: this.data.usuario.id,
        })
      : this.usuarioService.createUser(formValue);

    request$.subscribe({
      next: (response) => {
        this.isSaving = false;
        this.toastService.success('Usuario guardado correctamente');
        this.dialogRef.close(true); // o close(response)
      },
      error: (error) => {
        this.isSaving = false;
        this.toastService.error('Error al guardar usuario');
      },
    });
  }

  close(): void {
    this.dialogRef.close();
  }

  compareRoles(r1: Rol | null, r2: Rol | null): boolean {
    return !!r1 && !!r2 && r1.id === r2.id;
  }
}
