import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { AuthenticationRoutingModule } from './authentication-routing.module';

@NgModule({
  imports: [CommonModule, AuthenticationRoutingModule],
})
export class AuthenticationModule {}
