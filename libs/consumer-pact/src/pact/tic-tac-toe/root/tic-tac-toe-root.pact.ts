import { HttpHeaderKey, TicTacToeAuthority } from '@app/ui/shared/domain';
import { defaultTemplate } from '@app/ui/testing';
import { ContentType, ILink } from '@hal-form-client';
import { InteractionObject } from '@pact-foundation/pact';
import { JsonMap } from '@pact-foundation/pact/src/common/jsonTypes';
import { HTTPMethods } from '@pact-foundation/pact/src/common/request';
import { bearer } from '../../../utils/pact.utils';
import { jwtToken } from '../../../utils/token.utils';
import { adminCreateGameTemplate, createGameTemplate } from '../tic-tac-toe.mock';

const rootLinks: { [key: string]: ILink } & JsonMap = {
  self: { href: 'http://localhost/api/tic-tac-toe' },
  root: {
    href: 'http://localhost/api',
  },
  game: {
    href: 'http://localhost/api/tic-tac-toe/game/{gameId}',
    templated: true,
  },
  games: {
    href: 'http://localhost/api/tic-tac-toe/game{?myGames,isPrivate,player,status,page,size,sort}',
    templated: true,
  },
  players: {
    href: 'http://localhost/api/tic-tac-toe/player{?username}',
    templated: true,
  },
  create: {
    href: 'http://localhost/api/tic-tac-toe/game',
  },
  'ws:games': {
    href: '/ami/tic-tac-toe/game',
  },
  'ws:game:player': {
    href: '/ami/tic-tac-toe/game/player/{playerId}',
    templated: true,
  },
};

export namespace GetTicTacToeRootResource {
  export const unauthorized: InteractionObject = {
    state: 'stateless',
    uponReceiving: 'get tic-tac-toe root resource as unauthorized user',
    withRequest: {
      method: HTTPMethods.GET,
      path: '/api/tic-tac-toe',
      headers: {
        Accept: ContentType.APPLICATION_JSON_HAL_FORMS,
        Authorization: bearer(jwtToken()),
      },
    },
    willRespondWith: {
      status: 401,
      body: {
        reason: 'Unauthorized',
        title: 'Insufficient permissions',
      },
    },
  };

  export const successful_root: InteractionObject = {
    state: 'stateless',
    uponReceiving: 'get root resource with authority tic-tac-toe:root',
    withRequest: {
      method: HTTPMethods.GET,
      path: '/api/tic-tac-toe',
      headers: {
        Accept: ContentType.APPLICATION_JSON_HAL_FORMS,
        Authorization: bearer(jwtToken({ authorities: [TicTacToeAuthority.TIC_TAC_TOE_ROOT] })),
      },
    },
    willRespondWith: {
      status: 200,
      headers: { [HttpHeaderKey.CONTENT_TYPE]: ContentType.APPLICATION_JSON_HAL_FORMS },
      body: {
        _links: {
          ...rootLinks,
        },
        _templates: {
          ...(defaultTemplate as JsonMap),
          ...(createGameTemplate as JsonMap),
        },
      },
    },
  };

  export const successful_admin_create: InteractionObject = {
    state: 'stateless',
    uponReceiving: 'get root resource with authority tic-tac-toe:game:create',
    withRequest: {
      method: HTTPMethods.GET,
      path: '/api/tic-tac-toe',
      headers: {
        Accept: ContentType.APPLICATION_JSON_HAL_FORMS,
        Authorization: bearer(
          jwtToken({ authorities: [TicTacToeAuthority.TIC_TAC_TOE_ROOT, TicTacToeAuthority.TIC_TAC_TOE_GAME_CREATE] }),
        ),
      },
    },
    willRespondWith: {
      status: 200,
      headers: { [HttpHeaderKey.CONTENT_TYPE]: ContentType.APPLICATION_JSON_HAL_FORMS },
      body: {
        _links: {
          ...rootLinks,
        },
        _templates: {
          ...defaultTemplate,
          ...(adminCreateGameTemplate as JsonMap),
        },
      },
    },
  };
}
