import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ChangeDetectorRef } from '@angular/core';
import { of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { UserListComponent } from './user-list';
import { UserService } from '../../../services/user.service';

import { provideRouter } from '@angular/router';

describe('UserListComponent', () => {
  let component: UserListComponent;
  let fixture: ComponentFixture<UserListComponent>;

  let userServiceMock: {
    getUsers: ReturnType<typeof vi.fn>;
    deleteUser: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    userServiceMock = {
      getUsers: vi.fn(),
      deleteUser: vi.fn(),
    };

    userServiceMock.getUsers.mockReturnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [UserListComponent],
      providers: [
        provideRouter([]),
        {
          provide: UserService,
          useValue: userServiceMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserListComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call loadUsers on ngOnInit', () => {
    const spy = vi.spyOn(component, 'loadUsers');

    component.ngOnInit();

    expect(spy).toHaveBeenCalledOnce();
  });

  it('should load users into the users array', () => {
    const users = [
      {
        id: 1,
        name: 'Alan',
        email: 'alan@email.com',
      },
      {
        id: 2,
        name: 'John',
        email: 'john@email.com',
      },
    ];

    userServiceMock.getUsers.mockReturnValue(of(users));

    component.loadUsers();

    expect(userServiceMock.getUsers).toHaveBeenCalledOnce();
    expect(component.users).toEqual(users);
  });

  it('should call deleteUser service method', () => {
    userServiceMock.deleteUser.mockReturnValue(of(undefined));

    const loadUsersSpy = vi.spyOn(component, 'loadUsers');

    component.deleteUser(1);

    expect(userServiceMock.deleteUser).toHaveBeenCalledWith(1);
    expect(loadUsersSpy).toHaveBeenCalled();
  });

  it('should reload users after deletion', () => {
    userServiceMock.deleteUser.mockReturnValue(of(undefined));

    const loadUsersSpy = vi.spyOn(component, 'loadUsers');

    component.deleteUser(10);

    expect(loadUsersSpy).toHaveBeenCalledTimes(1);
  });
});
