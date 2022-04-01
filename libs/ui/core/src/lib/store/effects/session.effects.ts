import { Injectable } from '@angular/core';
import { Session } from '@app/domain';
import { SessionService } from '@app/ui/shared/app';
import { clearSession, initialize } from '@app/ui/shared/core';
import { createEffect, ofType } from '@ngneat/effects';
import { switchMap, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class SessionEffects {
  constructor(private readonly sessionService: SessionService) {}

  clearSession$ = createEffect((actions) =>
    actions.pipe(
      ofType(clearSession),
      switchMap(() => this.sessionService.clearSession()),
    ),
  );

  initialize$ = createEffect((actions) =>
    actions.pipe(
      ofType(initialize),
      tap(console.log),
      switchMap((props?: { session?: Session }) => this.sessionService.initialize(props?.session)),
    ),
  );
}