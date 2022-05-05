import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  AdministrationRelations,
  GlobalSettingsRelations,
  RoleManagementRelations,
  ServiceLogsRelations,
  UserManagementRelations,
} from '@app/ui/shared/domain';
import { HalFormService, Resource } from '@hal-form-client';
import { Observable, tap, throwError } from 'rxjs';
import { switchMap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AdministrationService extends HalFormService {
  constructor(protected override readonly httpClient: HttpClient, private readonly halFormService: HalFormService) {
    super(httpClient, '');
  }

  override initialize(): Observable<Resource> {
    return this.halFormService.getLink(AdministrationRelations.ADMINISTRATION_REL).pipe(
      switchMap((link) => {
        this._rootUrl = link?.href || '';
        return this._rootUrl?.length
          ? super.initialize()
          : throwError(() => new Error('Administration Initialization Error'));
      }),
      tap({ error: () => this.setResource(new Resource({})) }),
    );
  }

  hasUserManagementEmbedded(): Observable<boolean> {
    return this.hasEmbeddedObject(UserManagementRelations.USER_MANAGEMENT_REL);
  }

  hasRoleManagementEmbedded(): Observable<boolean> {
    return this.hasEmbeddedObject(RoleManagementRelations.ROLE_MANAGEMENT_REL);
  }

  hasServiceLogsLink(): Observable<boolean> {
    return this.hasLink(ServiceLogsRelations.SERVICE_LOGS_REL);
  }

  hasGlobalSettingsLink(): Observable<boolean> {
    return this.hasLink(GlobalSettingsRelations.GLOBAL_SETTINGS_REL);
  }
}