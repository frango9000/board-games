import { OverlayContainer } from '@angular/cdk/overlay';
import { ChangeDetectionStrategy, Component, HostBinding, OnInit } from '@angular/core';
import { SidebarRepository, ThemeRepository } from '@app/ui/shared/core';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'app-core',
  templateUrl: './core.component.html',
  styleUrls: ['./core.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CoreComponent implements OnInit {
  @HostBinding('class.dark-mode') darkModeClass = false;

  constructor(
    private readonly overlay: OverlayContainer,
    private readonly themeRepository: ThemeRepository,
    public readonly sidebarRepository: SidebarRepository,
  ) {}

  ngOnInit(): void {
    this._subscribeToThemeChanges();
  }

  private _subscribeToThemeChanges(): void {
    this.themeRepository.darkMode$.pipe(untilDestroyed(this)).subscribe((darkMode) => {
      const darkClassName = 'dark-mode';
      this.darkModeClass = !!darkMode;
      if (darkMode) {
        this.overlay.getContainerElement().classList.add(darkClassName);
      } else {
        this.overlay.getContainerElement().classList.remove(darkClassName);
      }
    });
  }
}
