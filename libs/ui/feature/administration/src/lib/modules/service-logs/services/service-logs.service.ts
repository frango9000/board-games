import { Injectable } from '@angular/core';
import { AdministrationRelations, ServiceLogs } from '@app/ui/shared/domain';
import { AdministrationService } from '@app/ui/shared/feature/administration';
import { Observable } from 'rxjs';
import { first, switchMap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class ServiceLogsService {
  constructor(private readonly administrationService: AdministrationService) {}

  public getServiceLogs(): Observable<ServiceLogs> {
    return this.administrationService.getLinkOrThrow(AdministrationRelations.SERVICE_LOGS_REL).pipe(
      first(),
      switchMap((link) => link.follow()),
    );
  }

  public deleteServiceLogs(serviceLogs: ServiceLogs) {
    return serviceLogs.affordTemplate({ template: AdministrationRelations.DELETE_SERVICE_LOGS_REL });
  }
}
