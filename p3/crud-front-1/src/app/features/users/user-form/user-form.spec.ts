import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { UserFormComponent } from './user-form';
import { UserService } from '../../../services/user.service';

describe('UserFormComponent', () => {
  let fixture: ComponentFixture<UserFormComponent>;
  let component: UserFormComponent;

  let userServiceMock: {
    getUser: ReturnType<typeof vi.fn>;
    createUser: ReturnType<typeof vi.fn>;
    updateUser: ReturnType<typeof vi.fn>;
  };

  let routerMock: {
    navigate: ReturnType<typeof vi.fn>;
  };

  let activatedRouteMock: {
    snapshot: {
      paramMap: {
        get: ReturnType<typeof vi.fn>;
      };
    };
  };

  beforeEach(async () => {
    userServiceMock = {
      getUser: vi.fn(),
      createUser: vi.fn(),
      updateUser: vi.fn(),
    };

    routerMock = {
      navigate: vi.fn(),
    };

    activatedRouteMock = {
      snapshot: {
        paramMap: {
          get: vi.fn().mockReturnValue(null),
        },
      },
    };

    await TestBed.configureTestingModule({
      imports: [UserFormComponent],
      providers: [
        {
          provide: UserService,
          useValue: userServiceMock,
        },
        {
          provide: Router,
          useValue: routerMock,
        },
        {
          provide: ActivatedRoute,
          useValue: activatedRouteMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserFormComponent);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the form with empty fields', () => {
    expect(component.form).toBeTruthy();
    expect(component.form.getRawValue()).toEqual({
      name: '',
      email: '',
    });
  });

  it('should initialize the form as invalid', () => {
    expect(component.form.invalid).toBe(true);
    expect(component.form.get('name')?.hasError('required')).toBe(true);
    expect(component.form.get('email')?.hasError('required')).toBe(true);
  });

  it('should mark the form as valid when valid values are provided', () => {
    component.form.setValue({
      name: 'Alan',
      email: 'alan@email.com',
    });

    expect(component.form.valid).toBe(true);
  });

  it('should mark the form as invalid when the email is invalid', () => {
    component.form.setValue({
      name: 'Alan',
      email: 'invalid-email',
    });

    expect(component.form.invalid).toBe(true);
    expect(component.form.get('email')?.hasError('email')).toBe(true);
  });

  it('should not load a user when the route has no id', () => {
    activatedRouteMock.snapshot.paramMap.get.mockReturnValue(null);

    fixture.detectChanges();

    expect(component.userId).toBeUndefined();
    expect(userServiceMock.getUser).not.toHaveBeenCalled();
  });

  it('should load and populate the form when the route has an id', () => {
    const existingUser = {
      id: 1,
      name: 'Alan',
      email: 'alan@email.com',
    };

    activatedRouteMock.snapshot.paramMap.get.mockReturnValue('1');
    userServiceMock.getUser.mockReturnValue(of(existingUser));

    fixture.detectChanges();

    expect(component.userId).toBe(1);
    expect(userServiceMock.getUser).toHaveBeenCalledOnce();
    expect(userServiceMock.getUser).toHaveBeenCalledWith(1);

    expect(component.form.getRawValue()).toEqual({
      name: 'Alan',
      email: 'alan@email.com',
    });
  });

  it('should convert the route id from string to number', () => {
    activatedRouteMock.snapshot.paramMap.get.mockReturnValue('25');

    userServiceMock.getUser.mockReturnValue(
      of({
        id: 25,
        name: 'John',
        email: 'john@email.com',
      }),
    );

    fixture.detectChanges();

    expect(component.userId).toBe(25);
    expect(typeof component.userId).toBe('number');
    expect(userServiceMock.getUser).toHaveBeenCalledWith(25);
  });

  it('should not call the service when the form is invalid', () => {
    component.form.setValue({
      name: '',
      email: '',
    });

    component.save();

    expect(userServiceMock.createUser).not.toHaveBeenCalled();
    expect(userServiceMock.updateUser).not.toHaveBeenCalled();
    expect(routerMock.navigate).not.toHaveBeenCalled();
  });
});
    