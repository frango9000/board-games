import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const loadTicTacToeHomeModule = () =>
  import('./modules/home/tic-tac-toe-home.module').then((m) => m.TicTacToeHomeModule);

const loadTicTacToeGameListModule = () =>
  import('./modules/games/game-list/tic-tac-toe-game-list.module').then((m) => m.TicTacToeGameListModule);

const loadTicTacToeGameListSidebarModule = () =>
  import('./modules/games/game-list-sidebar/tic-tac-toe-game-list-sidebar.module').then(
    (m) => m.TicTacToeGameListSidebarModule,
  );

const loadTicTacToeGameModule = () =>
  import('./modules/game/game/tic-tac-toe-game.module').then((m) => m.TicTacToeGameModule);

const loadTicTacToeGameSidebarModule = () =>
  import('./modules/game/game-sidebar/tic-tac-toe-game-sidebar.module').then((m) => m.TicTacToeGameSidebarModule);

const routes: Routes = [
  {
    path: '',
    loadChildren: loadTicTacToeHomeModule,
    pathMatch: 'full',
  },
  {
    path: 'games',
    data: { breadcrumb: { label: 'Games' } },
    children: [
      {
        path: '',
        children: [
          {
            path: '',
            loadChildren: loadTicTacToeGameListModule,
            pathMatch: 'full',
          },
          {
            path: '',
            loadChildren: loadTicTacToeGameListSidebarModule,
            pathMatch: 'full',
            outlet: 'sidebar',
          },
        ],
        pathMatch: 'full',
      },
      {
        path: ':gameId',
        data: { breadcrumb: { label: 'Game X1' } },
        children: [
          {
            path: '',
            loadChildren: loadTicTacToeGameModule,
          },
          {
            path: '',
            loadChildren: loadTicTacToeGameSidebarModule,
            outlet: 'sidebar',
          },
        ],
      },
    ],
  },
  { path: '**', redirectTo: '' },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TicTacToeRoutingModule {}
