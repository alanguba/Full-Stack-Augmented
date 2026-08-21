# Itera

Itera is a travel itinerary planner and generator built with Angular. It helps casual tourists and travelers create personalized travel plans by selecting destinations, travel duration, pace preferences, and places to visit. The app combines route planning, place discovery, and interactive map integration to produce itinerary suggestions powered by backend APIs.

## Overview

Itera enables users to:
- authenticate with email/password and OTP
- select a destination city
- search and add places to visit using Google Places autocomplete
- choose travel duration and pace
- generate itinerary suggestions
- save and review created itineraries
- access an admin configuration area for user management

## Key Features

- Itinerary generation based on destination, days, pace, and selected places
- Destination-based travel planning with autocomplete search
- Travel preference selection for pace and trip duration
- Place discovery through Google Places search
- Interactive map integration using Google Maps
- API-driven itinerary creation and plan save/load operations
- Responsive UI built with Angular Material components
- Route guards for authenticated access and role-based admin pages

## Tech Stack

- Angular 21.2.x
- TypeScript 5.9.x
- RxJS
- Angular Material
- `@angular/google-maps`
- Angular Router
- Angular standalone components
- Vitest (dev dependency)
- Prettier

## Prerequisites

- Node.js compatible with `npm@11.x`
- npm 11.x
- Angular CLI 21.2.7 or compatible

## Installation

1. Clone the repository:

```bash
git clone <repository-url>
cd itera-ui
```

2. Install dependencies:

```bash
npm install
```

3. Start the app:

```bash
npm run start
```

4. Open the app in your browser:

```text
http://localhost:4200/
```

## Running Locally

```bash
npm run start
```

Open the app at:

```text
http://localhost:4200/
```

### Notes

- The app currently loads Google Maps in `src/index.html`.
- Backend requests target `http://localhost:8080` in service classes.
- There is no active `environment.ts` config file in the repository.

## Available Scripts

- `npm run ng` - run Angular CLI commands
- `npm run start` - start the development server
- `npm run build` - build the app for production
- `npm run watch` - build in watch mode using the development configuration
- `npm run test` - run unit tests via Angular build unit-test configuration

## Project Structure

```
itera-ui/
+- angular.json
+- package.json
+- proxy.conf.json
+- public/
+- src/
   +- index.html
   +- main.ts
   +- material-theme.scss
   +- styles.css
   +- app/
      +- app.ts
      +- app.html
      +- app.css
      +- app.routes.ts
      +- app.config.ts
      +- components/
      +- guards/
      +- interceptors/
      +- models/
      +- pages/
      +- services/
      +- shared/
   +- environment/
```

- `src/app/components/` - reusable UI components, search inputs, map widgets, and presentation cards
- `src/app/pages/` - page-level views such as login, home, planes, itinerary, itinerary list, and admin pages
- `src/app/services/` - services for authentication, backend API calls, Google Places integration, token caching, and notifications
- `src/app/models/interfaces/` - typed interfaces for API payloads and domain models
- `src/app/guards/` - route guard logic for authentication, admin roles, and plan validation
- `src/app/interceptors/` - HTTP interceptors for auth headers and backend error handling
- `src/app/shared/` - shared header and layout components
- `src/app/app.routes.ts` - application route definitions
- `src/app/app.config.ts` - provider configuration and HTTP interceptor registration

## Architecture Overview

Itera uses an Angular standalone component architecture:
- Standalone components with explicit import declarations
- Router-driven navigation with nested routes under `/home`
- Service layer for backend and Google Places operations
- Angular Material UI components for cards, forms, lists, and navigation
- Reactive forms and component form controls for login and itinerary planning
- HTTP interceptors for JWT auth injection and centralized error handling
- LocalStorage token caching for session state

### Angular setup

The app uses `bootstrapApplication` from `src/main.ts` and a standalone component model for the root `App` component. The application is configured through `src/app/app.config.ts` with providers for routing, HTTP client, global error listeners, and zone change detection.

### Routing

Routing is defined in `src/app/app.routes.ts`.
- `login` is protected by `guestGuard`.
- `home` is the authenticated shell and contains child routes for `inicio`, `planes`, itinerary views, and admin configuration.
- `itinerario` requires a plan via `hasPlanGuard`.
- `configuracion` is protected by `roleGuard` and lazy-loads the admin user page.
- unsupported paths redirect to `login`.

### Service layer

Itera separates UI logic from data access using Angular services.
- `auth.service.ts` handles login, OTP, logout, and session state.
- `itinerary-api.service.ts` communicates with backend itinerary endpoints.
- `google-places.service.ts` wraps Google Places autocomplete and search.
- `token-cache.service.ts` manages JWT storage in `localStorage`.
- `toast.service.ts` provides notification support.

### Forms

The app relies on Angular reactive forms and form controls for user workflows.
- login screens gather credentials and OTP input.
- the plan creation flow captures destination, dates, pace, and selected places.
- form fields and validation are handled within page components and reusable inputs.

### Shared components

Shared UI pieces live under `src/app/shared/` and include the app header and common layout patterns. Reusable controls and display components are grouped under `src/app/components/`.

### Guards and interceptors

- `auth.guard.ts` protects authenticated routes.
- `guest.guard.ts` prevents logged-in users from accessing login routes.
- `role.guard.ts` restricts the admin configuration area to administrator roles.
- `has-plan.guard.ts` ensures itinerary routes require an existing plan.

HTTP interceptors are registered in `src/app/app.config.ts`:
- `auth.interceptor` attaches the JWT token to outgoing requests.
- `error.interceptor` centralizes backend error handling.

### API integration

HTTP integration uses Angular `HttpClient` with provider-level interceptors. Backend endpoints are currently referenced in service classes and target a local base address. Google Maps is also loaded via `src/index.html`, and the app relies on `@angular/google-maps` plus Google Places integration.

## External Integrations

- **Google Maps**: used to render maps and support place visualization
- **Google Places API**: used for city and place autocomplete suggestions and details
- **Backend REST APIs**: local backend endpoints are hardcoded to `http://localhost:8080` for login, itinerary generation, plan saving, role data, and user management

## UI / User Experience

The UI includes:
- a Material Design layout
- login and OTP authentication workflow
- destination selection and autocomplete search
- itinerary results and place lists
- map interaction for selected places
- admin interface for user management

## Build

```bash
npm run build
```

Production artifacts are emitted to the default Angular CLI `dist/` folder.

## Testing

- Unit tests: `npm run test`
- No dedicated `e2e` script is defined in `package.json`
- Detected test tooling: `Vitest`

## Linting / Code Quality

- No lint script is defined in `package.json`
- Formatting tooling: `Prettier`

## Screenshots

<img width="1693" height="1261" alt="image" src="https://github.com/user-attachments/assets/4d0aa603-6247-4788-bc30-e6e06e78d942" />
<img width="1699" height="1268" alt="image" src="https://github.com/user-attachments/assets/96138c52-d832-407e-b13d-8295bf75ed02" />
<img width="1712" height="1261" alt="image" src="https://github.com/user-attachments/assets/a3023fe4-9452-42ed-a25c-235bbde73431" />


## Troubleshooting

- If `npm run start` fails, rerun `npm install` and verify dependencies installed successfully.
- If the app does not load maps, inspect `src/index.html` for the Google Maps script URL and verify the API key is valid.
- If backend requests fail, confirm the backend service is running and reachable at `http://localhost:8080`.
- If login does not work, clear the browser `localStorage` and retry because JWT state is cached locally.
- If a route redirects unexpectedly, verify the auth state and route guard conditions in `src/app/guards/`.
- Because this repository does not include active environment files, external configuration values must be provided manually and not committed directly.

## Contributing

Contributions are welcome. Please open issues for bugs and feature requests, and submit pull requests with clear descriptions.

## License

This project is licensed under the MIT License © 2026 Alan Gutierrez.

You are free to use, modify, and distribute this software with proper attribution.  
See the [LICENSE](./LICENSE) file for more details.
