import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Breakpoint, BreakpointFilter, CoreContextMenuService } from '@app/ui/shared/core';
import { TicTacToeGameChangedMessageAction } from '@app/ui/shared/domain';
import { TicTacToeService } from '@app/ui/shared/feature/tic-tac-toe';
import { TranslocoService } from '@ngneat/transloco';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { throttleTime } from 'rxjs';
import { TicTacToeGameListFilterService } from '../../../services/tic-tac-toe-game-list-filter.service';
import { TicTacToeGameListDatasource } from './tic-tac-toe-game-list.datasource';

@UntilDestroy()
@Component({
  selector: 'app-tic-tac-toe-game-list',
  templateUrl: './tic-tac-toe-game-list.component.html',
  styleUrls: ['./tic-tac-toe-game-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TicTacToeGameListComponent implements OnInit, OnDestroy {
  private readonly service: TicTacToeService = inject(TicTacToeService);
  private readonly filterService: TicTacToeGameListFilterService = inject(TicTacToeGameListFilterService);
  private readonly contextMenuService: CoreContextMenuService = inject(CoreContextMenuService);
  private readonly translocoService: TranslocoService = inject(TranslocoService);
  public readonly datasource: TicTacToeGameListDatasource = new TicTacToeGameListDatasource(this.service);
  public readonly reactiveColumns: BreakpointFilter[] = [
    { value: 'status' },
    { value: 'isPrivate', breakpoint: Breakpoint.S },
    { value: 'playerX' },
    { value: 'playerO' },
    { value: 'lastActivityAt', breakpoint: Breakpoint.S },
    { value: 'edit' },
  ];
  protected TRANSLOCO_SCOPE = 'tic-tac-toe.game-list';

  @ViewChild(MatPaginator) set paginator(paginator: MatPaginator) {
    paginator?.page.pipe(untilDestroyed(this)).subscribe((page) => (this.datasource.page = page));
  }

  @ViewChild(MatSort) set sort(matSort: MatSort) {
    matSort?.sortChange.pipe(untilDestroyed(this)).subscribe((sort) => (this.datasource.sort = sort));
  }

  ngOnInit(): void {
    this.filterService.filters$
      .pipe(untilDestroyed(this), throttleTime(300, undefined, { leading: true, trailing: true }))
      .subscribe((filters) => (this.datasource.filters = filters));
    this.contextMenuService.show([
      {
        id: 'create-new-game-option',
        icon: 'not_started',
        title$: this.translocoService.selectTranslate(`${this.TRANSLOCO_SCOPE}.context.create`, {}),
        route: '/tic-tac-toe/new-game',
      },
    ]);

    this._subscribeToGameEvents();
  }

  ngOnDestroy(): void {
    this.contextMenuService.reset();
  }

  private _subscribeToGameEvents() {
    this.service
      .gamesChangedEvents()
      .pipe(untilDestroyed(this))
      .subscribe((message) => {
        if (message.action === TicTacToeGameChangedMessageAction.CREATED) {
          this.datasource.refresh();
        } else {
          this.datasource.refreshById(message.gameId);
        }
      });
  }
}
