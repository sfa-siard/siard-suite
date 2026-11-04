import { beforeEach, describe, expect, it, vi } from 'vitest'

vi.stubGlobal('window', { location: { search: '?token=test-token-123' } })

const fetchMock = vi.fn(async () => new Response('{}', { status: 200 }))
vi.stubGlobal('fetch', fetchMock)

const { getToken, apiFetch, openArchive } = await import('./api')

beforeEach(() => {
  fetchMock.mockClear()
})

describe('getToken', () => {
  it('reads the token from the launch URL query string', () => {
    expect(getToken()).toBe('test-token-123')
  })
})

describe('apiFetch', () => {
  it('sends the session token as a bearer header', async () => {
    await apiFetch('/api/archives/1/tree')
    const [url, init] = fetchMock.mock.calls[0]
    expect(url).toBe('/api/archives/1/tree')
    expect((init.headers as Headers).get('Authorization')).toBe('Bearer test-token-123')
  })
})

describe('openArchive', () => {
  it('posts the path as JSON with the token attached', async () => {
    await openArchive('/tmp/a.siard')
    const [url, init] = fetchMock.mock.calls[0]
    expect(url).toBe('/api/archives')
    expect(init.method).toBe('POST')
    expect((init.headers as Headers).get('Authorization')).toBe('Bearer test-token-123')
  })
})
