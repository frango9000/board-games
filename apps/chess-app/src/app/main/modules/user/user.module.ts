import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { UserMenuComponent } from './components/user-menu/user-menu.component';
import { UserSidenavComponent } from './components/user-sidenav/user-sidenav.component';

import { UserRoutingModule } from './user-routing.module';

@NgModule({
  declarations: [UserMenuComponent, UserSidenavComponent],
  imports: [CommonModule, UserRoutingModule],
})
export class UserModule {}
