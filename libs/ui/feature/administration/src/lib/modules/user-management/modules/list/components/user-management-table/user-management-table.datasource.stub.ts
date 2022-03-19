import { Injectable } from '@angular/core';
import { UserPage } from '@app/domain';
import { BehaviorSubject, noop, of } from 'rxjs';
import { UserManagementTableDatasource } from './user-management-table.datasource';

@Injectable({ providedIn: 'root' })
export class StubUserManagementTableDatasource implements Partial<UserManagementTableDatasource> {
  userPage$ = of({ isAllowedTo: () => true }) as BehaviorSubject<UserPage>;
  disconnect = noop;
  connect = () => of([]);
}

export const stubUserManagementTableDatasourceProvider = {
  provide: UserManagementTableDatasource,
  useClass: StubUserManagementTableDatasource,
};
