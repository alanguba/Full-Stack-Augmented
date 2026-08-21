import { CommonModule } from '@angular/common';
import { Component, inject, Input, signal } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
  AbstractControl,
  ValidationErrors,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ResetPasswordService } from '../../services/reset-password.service';
import { finalize } from 'rxjs';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-login-forgot-password',
  imports: [
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    ReactiveFormsModule,
    MatProgressSpinnerModule,
    CommonModule,
    MatIconModule,
    MatSnackBarModule,
  ],
  templateUrl: './login-forgot-password.component.html',
  styleUrl: './login-forgot-password.component.css',
})
export class LoginForgotPasswordComponent {
  private readonly fb = inject(FormBuilder);
  private readonly resetPasswordService = inject(ResetPasswordService);
    private toastService = inject(ToastService);

  readonly hidePassword = signal(true);
  readonly loadingFirstStep = signal(false);
  readonly loadingOtpStep = signal(false);
  readonly otpVisible = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);

  @Input({ required: true }) toogleHandler!: () => void;

  private passwordsMatchValidator = (group: AbstractControl): ValidationErrors | null => {
    const password = group.get('password');
    const confirm = group.get('confirmPassword');

    if (!password || !confirm) return null;

    // If other validators on confirmPassword have found errors, don't overwrite them
    const confirmErrors = confirm.errors ? { ...confirm.errors } : null;

    if (password.value !== confirm.value) {
      confirm.setErrors({ ...confirmErrors, passwordMismatch: true });
      return { passwordMismatch: true };
    }

    // clear passwordMismatch but preserve other errors
    if (confirmErrors) {
      delete (confirmErrors as any).passwordMismatch;
      const hasOther = Object.keys(confirmErrors).length > 0;
      confirm.setErrors(hasOther ? confirmErrors : null);
    } else {
      confirm.setErrors(null);
    }

    return null;
  };

  readonly form = this.fb.nonNullable.group(
    {
      email: ['', [Validators.required, Validators.email]],
      otp: [{ value: '', disabled: true }, [Validators.required, Validators.pattern(/^\d{4}$/)]],
      password: ['', [Validators.required, Validators.minLength(5)]],
      confirmPassword: ['', [Validators.required, Validators.minLength(5)]],
    },
    { validators: [this.passwordsMatchValidator] },
  );

  requestOtp(): void {
    this.errorMessage.set(null);
    this.successMessage.set(null);

    const emailControl = this.form.controls.email;

    emailControl.markAsTouched();

    if (emailControl.invalid) {
      return;
    }

    const email = emailControl.getRawValue().trim();

    this.loadingFirstStep.set(true);

    this.resetPasswordService.firstStepResetPassword(email).subscribe({
      next: () => {
        this.otpVisible.set(true);
        this.form.controls.otp.enable();
        this.form.controls.otp.reset('');
        this.successMessage.set('Ingresa el código OTP de 4 dígitos y tu nueva contraseña.');
      },
      error: (err) => {
        this.otpVisible.set(false);
        this.form.controls.otp.disable();
        this.form.controls.otp.reset('');
        this.errorMessage.set(err?.error?.message ?? 'No fue posible validar tus credenciales.');
      },
    });
  }

  submitNewPassword(): void {
    this.errorMessage.set(null);
    this.successMessage.set(null);

    const emailControl = this.form.controls.email;
    const passwordControl = this.form.controls.password;
    const otpControl = this.form.controls.otp;
    const confirmPasswordControl = this.form.controls.confirmPassword;

    emailControl.markAsTouched();
    passwordControl.markAsTouched();
    otpControl.markAsTouched();
    confirmPasswordControl.markAsTouched();

    if (
      emailControl.invalid ||
      passwordControl.invalid ||
      confirmPasswordControl.invalid ||
      otpControl.invalid
    ) {
      return;
    }

    const email = emailControl.getRawValue().trim();
    const password = passwordControl.getRawValue();
    const otp = otpControl.getRawValue();

    this.loadingOtpStep.set(true);

    this.resetPasswordService
      .resetPasswrod(email, password, otp)
      .pipe(finalize(() => this.loadingOtpStep.set(false)))
      .subscribe({
        next: (response) => {
          this.toastService.success('Contraseña actualizada correctamente');

          this.resetForm();
          this.toogleHandler();
        },
        error: (err) => {
          this.errorMessage.set(err?.error?.message ?? 'OTP inválido o expirado.');
        },
      });
  }

  onlyDigits(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/\D/g, '').slice(0, 4);
    this.form.controls.otp.setValue(input.value, { emitEvent: false });
  }

  resetForm(): void {
    this.form.reset({
      email: '',
      password: '',
      confirmPassword: '',
      otp: '',
    });

    this.form.controls.otp.disable();
  }
}
