import { MessageConsumerPact, Pact } from '@pact-foundation/pact';
import { RegexMatcher, term, UUID_V4_FORMAT } from '@pact-foundation/pact/src/dsl/matchers';
import { resolve } from 'path';

export function pactForResource(resource: string): Pact {
  return new Pact({
    consumer: `app-${resource}`,
    provider: 'api',
    log: resolve(process.cwd(), 'coverage', 'pact', 'logs', 'api.log'),
    logLevel: 'error',
    dir: resolve(process.cwd(), 'apps', 'api', 'target', 'test-classes', 'pact', resource + 'Controller'),
    cors: true,
    timeout: 10000,
    spec: 3,
    pactfileWriteMode: 'merge',
  });
}

export function pactForMessages(resource: string): MessageConsumerPact {
  return new MessageConsumerPact({
    consumer: `app-${resource}`,
    provider: 'ami',
    log: resolve(process.cwd(), 'coverage', 'pact', 'logs', 'ami.log'),
    logLevel: 'error',
    dir: resolve(process.cwd(), 'apps', 'api', 'target', 'test-classes', 'pact-messages', resource + 'Messages'),
    spec: 3,
    pactfileWriteMode: 'merge',
  });
}

export const BEARER_TOKEN_REGEX = /^Bearer\s([a-zA-Z0-9_=]+)\.([a-zA-Z0-9_=]+)\.([a-zA-Z0-9_\-+/=]*)$/;

export function bearer(token?: string): RegexMatcher<string> {
  const defaultToken =
    'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c';
  return term({
    generate: `Bearer ${token || defaultToken}`,
    matcher: BEARER_TOKEN_REGEX.source,
  });
}

export const JWT_TOKEN_REGEX = /^([a-zA-Z0-9_=]+)\.([a-zA-Z0-9_=]+)\.([a-zA-Z0-9_\-+/=]*)$/;

export function jwt(token?: string): RegexMatcher<string> {
  const defaultToken =
    'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c';
  return term({
    generate: token || defaultToken,
    matcher: JWT_TOKEN_REGEX.source,
  });
}

export function withUuid(string?: string): RegexMatcher<string> {
  let generate = string || '/{uuid}';
  generate = generate.replace(/{uuid}/, 'ce118b6e-d8e1-11e7-9296-cec278b6b50a');
  let matcher = string || '/{uuid}';
  matcher = matcher.replace(/[-/\\^$*+?.()|[\]{}]/g, '\\$&');
  matcher = matcher.replace(/\\{uuid\\}/, UUID_V4_FORMAT.slice(1, -1));
  matcher = `^${matcher}$`;
  return term({
    generate,
    matcher,
  });
}
