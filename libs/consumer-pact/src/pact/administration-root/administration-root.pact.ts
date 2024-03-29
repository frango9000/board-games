import { AdminAuthority, AuthorityAuthority, HttpHeaderKey, RoleAuthority, UserAuthority } from '@app/ui/shared/domain';
import { defaultTemplate } from '@app/ui/testing';
import { ContentType } from '@hal-form-client';
import { InteractionObject } from '@pact-foundation/pact';
import { HTTPMethods } from '@pact-foundation/pact/src/common/request';
import { bearer } from '../../utils/pact.utils';
import { jwtToken } from '../../utils/token.utils';

export namespace GetAdministrationRootResource {
  export const as_unauthorized: InteractionObject = {
    state: 'stateless',
    uponReceiving: 'get administration root resource as unauthorized user',
    withRequest: {
      method: HTTPMethods.GET,
      path: '/api/administration',
      headers: {
        Accept: ContentType.APPLICATION_JSON_HAL_FORMS,
      },
    },
    willRespondWith: {
      status: 403,
      body: {
        reason: 'Forbidden',
        title: 'Authentication required',
      },
    },
  };

  export const as_authorized: InteractionObject = {
    state: 'stateless',
    uponReceiving: 'get administration root resource as authorized',
    withRequest: {
      method: HTTPMethods.GET,
      path: '/api/administration',
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

  export const as_admin_root: InteractionObject = {
    state: 'stateless',
    uponReceiving: 'get administration root resource as admin:root',
    withRequest: {
      method: HTTPMethods.GET,
      path: '/api/administration',
      headers: {
        Accept: ContentType.APPLICATION_JSON_HAL_FORMS,
        Authorization: bearer(jwtToken({ authorities: [AdminAuthority.ADMIN_ROOT] })),
      },
    },
    willRespondWith: {
      status: 200,
      headers: { [HttpHeaderKey.CONTENT_TYPE]: ContentType.APPLICATION_JSON_HAL_FORMS },
      body: {
        _links: {
          self: {
            href: 'http://localhost/api/administration',
          },
        },
        _templates: { ...defaultTemplate },
      },
    },
  };

  export const as_admin_root__service_logs_read: InteractionObject = {
    state: 'stateless',
    uponReceiving: 'get administration root resource as admin:root service-logs:read',
    withRequest: {
      method: HTTPMethods.GET,
      path: '/api/administration',
      headers: {
        Accept: ContentType.APPLICATION_JSON_HAL_FORMS,
        Authorization: bearer(jwtToken({ authorities: [AdminAuthority.ADMIN_ROOT, 'service-logs:read'] })),
      },
    },
    willRespondWith: {
      status: 200,
      headers: { [HttpHeaderKey.CONTENT_TYPE]: ContentType.APPLICATION_JSON_HAL_FORMS },
      body: {
        _links: {
          self: {
            href: 'http://localhost/api/administration',
          },
          'service-logs': {
            href: 'http://localhost/api/administration/service-logs',
          },
        },
        _templates: { ...defaultTemplate },
      },
    },
  };

  export const as_admin_root__global_settings_read: InteractionObject = {
    state: 'stateless',
    uponReceiving: 'get administration root resource as admin:root global-settings:read',
    withRequest: {
      method: HTTPMethods.GET,
      path: '/api/administration',
      headers: {
        Accept: ContentType.APPLICATION_JSON_HAL_FORMS,
        Authorization: bearer(jwtToken({ authorities: [AdminAuthority.ADMIN_ROOT, 'global-settings:read'] })),
      },
    },
    willRespondWith: {
      status: 200,
      headers: { [HttpHeaderKey.CONTENT_TYPE]: ContentType.APPLICATION_JSON_HAL_FORMS },
      body: {
        _links: {
          self: {
            href: 'http://localhost/api/administration',
          },
          'global-settings': {
            href: 'http://localhost/api/global-settings',
          },
        },
        _templates: { ...defaultTemplate },
      },
    },
  };

  export const as_admin_user_management_root: InteractionObject = {
    state: 'stateless',
    uponReceiving:
      'get administration root resource with user management embedded as admin:root admin:user-management:root user:create',
    withRequest: {
      method: HTTPMethods.GET,
      path: '/api/administration',
      headers: {
        Accept: ContentType.APPLICATION_JSON_HAL_FORMS,
        Authorization: bearer(
          jwtToken({
            authorities: [
              AdminAuthority.ADMIN_ROOT,
              AdminAuthority.ADMIN_USER_MANAGEMENT_ROOT,
              UserAuthority.USER_READ,
              UserAuthority.USER_CREATE,
            ],
          }),
        ),
      },
    },
    willRespondWith: {
      status: 200,
      headers: { [HttpHeaderKey.CONTENT_TYPE]: ContentType.APPLICATION_JSON_HAL_FORMS },
      body: {
        _links: {
          self: {
            href: 'http://localhost/api/administration',
          },
        },
        _embedded: {
          'user-management': {
            _links: {
              self: {
                href: 'http://localhost/api/administration',
              },
              user: {
                href: 'http://localhost/api/user/{userId}',
                templated: true,
              },
              users: {
                href: 'http://localhost/api/user{?search,page,size,sort}',
                templated: true,
              },
            },
            _templates: {
              default: {
                method: 'HEAD',
                properties: [],
              },
              create: {
                method: 'POST',
                properties: [
                  {
                    name: 'active',
                  },
                  {
                    name: 'authorityIds',
                  },
                  {
                    name: 'credentialsExpired',
                  },
                  {
                    name: 'email',
                    type: 'email',
                  },
                  {
                    name: 'expired',
                  },
                  {
                    name: 'firstname',
                    type: 'text',
                  },
                  {
                    name: 'lastname',
                    type: 'text',
                  },
                  {
                    name: 'locked',
                  },
                  {
                    name: 'password',
                    minLength: 6,
                    maxLength: 128,
                    type: 'text',
                  },
                  {
                    name: 'profileImageUrl',
                    type: 'text',
                  },
                  {
                    name: 'roleId',
                    type: 'text',
                  },
                  {
                    name: 'username',
                    minLength: 5,
                    maxLength: 128,
                    type: 'text',
                  },
                ],
                target: 'http://localhost/api/user',
              },
            },
          },
        },
        _templates: { ...defaultTemplate },
      },
    },
  };

  export const as_admin_role_management_root: InteractionObject = {
    state: 'stateless',
    uponReceiving:
      'get administration root resource with role management embedded as admin:root admin:role-management:root role:create',
    withRequest: {
      method: HTTPMethods.GET,
      path: '/api/administration',
      headers: {
        Accept: ContentType.APPLICATION_JSON_HAL_FORMS,
        Authorization: bearer(
          jwtToken({
            authorities: [
              AdminAuthority.ADMIN_ROOT,
              AdminAuthority.ADMIN_ROLE_MANAGEMENT_ROOT,
              RoleAuthority.ROLE_READ,
              RoleAuthority.ROLE_CREATE,
              AuthorityAuthority.AUTHORITY_READ,
            ],
          }),
        ),
      },
    },
    willRespondWith: {
      status: 200,
      headers: { [HttpHeaderKey.CONTENT_TYPE]: ContentType.APPLICATION_JSON_HAL_FORMS },
      body: {
        _links: {
          self: {
            href: 'http://localhost/api/administration',
          },
        },
        _embedded: {
          'role-management': {
            _links: {
              self: {
                href: 'http://localhost/api/administration',
              },
              role: {
                href: 'http://localhost/api/role/{roleId}',
                templated: true,
              },
              roles: {
                href: 'http://localhost/api/role{?search,page,size,sort}',
                templated: true,
              },
              authorities: {
                href: 'http://localhost/api/authority{?page,size,sort}',
                templated: true,
              },
            },
            _templates: {
              default: {
                method: 'HEAD',
                properties: [],
              },
              create: {
                method: 'POST',
                properties: [
                  {
                    name: 'name',
                    required: true,
                    minLength: 3,
                    maxLength: 128,
                    type: 'text',
                  },
                ],
                target: 'http://localhost/api/role',
              },
            },
          },
        },
        _templates: { ...defaultTemplate },
      },
    },
  };
}
