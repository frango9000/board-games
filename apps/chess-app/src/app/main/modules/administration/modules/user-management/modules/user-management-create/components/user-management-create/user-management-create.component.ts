import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Role } from '@chess-lite/domain';
import { tadaAnimation, wobbleAnimation } from 'angular-animations';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { HeaderService } from '../../../../../../../../../core/services/header.service';
import { matchingControlsValidators } from '../../../../../../../../../core/utils/forms/validators/matching-controls.validator';
import { UserManagementService } from '../../../../services/user-management.service';

@Component({
  selector: 'chess-lite-user-management-create',
  templateUrl: './user-management-create.component.html',
  styleUrls: ['./user-management-create.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  animations: [wobbleAnimation(), tadaAnimation()],
})
export class UserManagementCreateComponent implements OnDestroy {
  roles: Observable<Role[]> = this.activatedRoute.data.pipe(map((data) => data.roles));

  public form = new FormGroup(
    {
      username: new FormControl(''),
      email: new FormControl(''),
      firstname: new FormControl(''),
      lastname: new FormControl(''),
      profileImageUrl: new FormControl(''),
      roleId: new FormControl(''),
      password: new FormControl(''),
      password2: new FormControl(''),
      active: new FormControl(false),
      locked: new FormControl(false),
      expired: new FormControl(false),
      credentialsExpired: new FormControl(false),
    },
    [matchingControlsValidators('password', 'password2')],
  );

  submitError = false;
  submitSuccess = false;
  submitSuccessMessage = false;
  submitErrorMessage = false;

  private readonly routeUp = ['administration', 'user-management'];

  constructor(
    private readonly headerService: HeaderService,
    private readonly userManagementService: UserManagementService,
    private readonly activatedRoute: ActivatedRoute,
    private readonly cdr: ChangeDetectorRef,
    private readonly router: Router,
  ) {
    this.headerService.setHeader({ title: 'New User', navigationLink: this.routeUp });
  }

  ngOnDestroy(): void {
    this.headerService.resetHeader();
  }

  onSubmit() {
    this.userManagementService.createUser(this.form.value).subscribe({
      next: () => this.setSubmitStatus(true),
      error: () => this.setSubmitStatus(false),
    });
  }

  setSubmitStatus(success: boolean) {
    this.submitSuccess = success;
    this.submitSuccessMessage = success;
    this.submitError = !success;
    this.submitErrorMessage = !success;
    this.cdr.markForCheck();
    setTimeout(() => {
      this.router.navigate(this.routeUp);
    }, 2000);
  }
}
