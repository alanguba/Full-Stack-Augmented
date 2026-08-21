import { Routes } from '@angular/router';
import { HomePage } from './pages/home/home.page';
import { InicioPage } from './pages/inicio/inicio.page';
import { PlanesPage } from './pages/planes/planes.page';
import { ItinerarioPage } from './pages/itinerario/itinerario.page';
import { hasPlanGuard } from './components/guards/has-plan.guard';
import { ItinerarioListPage } from './pages/itinerario-list/itinerario-list.page';
import { ItinerarioDetalleComponente } from './pages/itinerario-detalle/itinerario-detalle.componente';
import { LoginPage } from './pages/login/login.page';
import { authGuard } from './guards/auth.guard';
import { guestGuard } from './guards/guest.guard';
import { AdminHome } from './pages/admin/admin-home/admin-home';
import { roleGuard } from './guards/role.guard';

export const routes: Routes = [
  { path: 'login', component: LoginPage, canActivate: [guestGuard] },
  {
    path: 'home',
    component: HomePage,
    children: [
      { path: 'inicio', component: InicioPage },
      { path: 'planes', component: PlanesPage },
      { path: 'itinerario', component: ItinerarioPage, canActivate: [hasPlanGuard] },
      { path: 'mis-itinerarios', component: ItinerarioListPage },
      { path: 'itinerario/:id', component: ItinerarioDetalleComponente },
      {
        path: 'configuracion',
        component: AdminHome,
        children: [
          {
            path: '',
            redirectTo: 'usuarios',
            pathMatch: 'full',
          },
          {
            path: 'usuarios',
            loadComponent: () =>
              import('./pages/admin/usuario/usuario.page').then((m) => m.UsuarioPage),
          },
        ],
        canActivate: [roleGuard],
        data: { roles: ['ROLE_ADMINISTRADOR'] },
      },
    ],
    canActivate: [authGuard],
  },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' },
];
