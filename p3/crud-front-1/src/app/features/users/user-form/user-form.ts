import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { UserService } from '../../../services/user.service';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-form.html',
})
export class UserFormComponent implements OnInit {
  userId?: number;
  form: any;

  constructor(
    private fb: FormBuilder,
    private service: UserService,
    private router: Router,
    private route: ActivatedRoute,
  ) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
    });
  }
  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.userId = Number(id);

      this.service.getUser(this.userId).subscribe((user) => {
        this.form.patchValue(user);
      });
    }
  }

  save(): void {
    if (this.form.invalid) {
      return;
    }

    const user = this.form.getRawValue();

    if (this.userId) {
      this.service.updateUser(this.userId, user).subscribe(() => this.router.navigate(['/users']));
    } else {
      this.service.createUser(user).subscribe(() => this.router.navigate(['/users']));
    }
  }
}
