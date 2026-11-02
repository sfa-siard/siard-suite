import { useState } from 'react'
import { fetchTree, openArchive, type SchemaDto } from './api'
import { pickFile } from './api'

export default function App() {
  const [path, setPath] = useState('')
  const [schemas, setSchemas] = useState<SchemaDto[] | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  async function open(target: string) {
    setLoading(true)
    setError(null)
    setSchemas(null)
    try {
      const opened = await openArchive(target)
      const tree = await fetchTree(opened.id)
      setSchemas(tree.schemas)
    } catch (e) {
      setError(e instanceof Error ? e.message : String(e))
    } finally {
      setLoading(false)
    }
  }

  async function handleOpen() {
    await open(path)
  }

  async function handleBrowse() {
    const selected = await pickFile();
    if (selected) {
      setPath(selected);
      await open(selected);
    }
  }

  return (
    <main style={{ fontFamily: 'sans-serif', maxWidth: '60rem', margin: '2rem auto' }}>
      <h1>SIARD Web Companion</h1>
      <p>Open a SIARD archive on this machine to browse its structure.</p>
      <div>
        <input
          type="text"
          value={path}
          onChange={(e) => setPath(e.target.value)}
          placeholder="/path/to/archive.siard"
          style={{ width: '30rem', marginRight: '0.5rem' }}
          aria-label="Archive path"
        />
        <button onClick={handleOpen} disabled={loading || path.trim() === ''}>
          {loading ? 'Opening…' : 'Open'}
        </button>
        <button onClick={handleBrowse} disabled={loading} style={{ marginLeft: '0.5rem' }}>
          Browse…
        </button>
      </div>
      {error && (
        <p data-testid="error-message" role="alert" style={{ color: 'crimson' }}>
          {error}
        </p>
      )}
      {schemas && (
        <ul data-testid="archive-tree">
          {schemas.map((schema) => (
            <li key={schema.name}>
              <strong>{schema.name}</strong>
              <ul>
                {schema.tables.map((table) => (
                  <li key={table.name}>
                    {table.name} ({table.rowCount} rows)
                    <ul>
                      {table.columns.map((column) => (
                        <li key={column.name}>
                          {column.name}: {column.type ?? 'unknown'}
                        </li>
                      ))}
                    </ul>
                  </li>
                ))}
              </ul>
            </li>
          ))}
        </ul>
      )}
    </main>
  )
}
