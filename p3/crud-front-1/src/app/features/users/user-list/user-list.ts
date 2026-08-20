import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UserService } from '../../../services/user.service';
import { User } from '../../../model/User.interface';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './user-list.html'
})
export class UserListComponent implements OnInit {

  users: User[] = [];

  constructor(private userService: UserService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
  this.userService.getUsers().subscribe({
    next: users => {
      this.users = users;
      this.cdr.detectChanges();
    }
  });
}

  deleteUser(id: number): void {
    this.userService.deleteUser(id)
      .subscribe(() => this.loadUsers());
  }
}