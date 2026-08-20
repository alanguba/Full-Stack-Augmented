import { Routes } from '@angular/router';
import { UserListComponent } from './features/users/user-list/user-list';
import { UserFormComponent } from './features/users/user-form/user-form';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'users',
    pathMatch: 'full'
  },
  {
    path: 'users',
    component: UserListComponent
  },
  {
    path: 'users/new',
    component: UserFormComponent
  },
  {
    path: 'users/edit/:id',
    component: UserFormComponent
  }
];