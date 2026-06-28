import { useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { Page } from '../../components/page/page'
import { Button } from '../../components/button/button'
import { useAsync } from '../../hooks/use-async'
import { useDebounce } from '../../hooks/use-debounce'
import { api } from '../../services/api'
import { AnnotationPanel } from '../../components/annotation-panel/annotation-panel'
import styles from './resources.module.css'

interface ContentItem {
  id: string
  title: string
  description: string | null
  contentType: string
  uploaderName: string | null
}

const TYPES = [
  { value: 'ARTICLE', label: 'Article' },
  { value: 'RESEARCH', label: 'Research' },
  { value: 'application/pdf', label: 'PDF' },
]

export function ResourcesPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const search = searchParams.get('search') ?? ''
  const [selectedType, setSelectedType] = useState<string | null>(null)
  const [annotatingId, setAnnotatingId] = useState<string | null>(null)
  const [showUpload, setShowUpload] = useState(false)
  const [uploadTitle, setUploadTitle] = useState('')
  const [uploadDesc, setUploadDesc] = useState('')
  const [uploadType, setUploadType] = useState('ARTICLE')
  const [uploadFile, setUploadFile] = useState<File | null>(null)
  const [saveError, setSaveError] = useState<string | null>(null)

  const debouncedSearch = useDebounce(search)

  function buildUrl() {
    const params = new URLSearchParams()
    if (debouncedSearch) params.set('keyword', debouncedSearch)
    if (selectedType) params.set('contentType', selectedType)
    const query = params.toString()
    return query ? `/api/resources?${query}` : '/api/resources'
  }

  const { data, reload } = useAsync<{ content: ContentItem[] }>(
    () => api.get<{ content: ContentItem[] }>(buildUrl()),
    [debouncedSearch, selectedType],
  )
  const items = data?.content ?? []

  const { data: recs = [] } = useAsync<ContentItem[]>(
    () => api.get<ContentItem[]>('/api/recommendations'),
  )

  async function handleUpload() {
    if (!uploadFile || !uploadTitle) return
    const formData = new FormData()
    formData.append('file', uploadFile)
    formData.append('title', uploadTitle)
    formData.append('description', uploadDesc)
    formData.append('contentType', uploadType)
    await api.upload('/api/resources', formData)
    setShowUpload(false)
    setUploadTitle('')
    setUploadDesc('')
    setUploadFile(null)
    reload()
  }

  async function openFile(id: string) {
    const res = await api.get<{ url: string }>(`/api/resources/${id}/download`)
    window.open(res.url, '_blank')
  }

  async function saveToList(id: string) {
    try {
      await api.post('/api/reading-list/items', { contentId: id })
      setSaveError(null)
    } catch (e) {
      setSaveError(e instanceof Error ? e.message : 'Could not save to list')
    }
  }

  return (
    <Page>
      <div className={styles.container}>
        <div className={styles.toolbar}>
          <input
            className={styles.search}
            placeholder="Search resources..."
            value={search}
            onChange={(e) => setSearchParams(e.target.value ? { search: e.target.value } : {})}
          />
          <Button variant="outlined" onClick={() => setShowUpload(!showUpload)}>
            Upload
          </Button>
        </div>

        <div className={styles.filters}>
          <button
            className={selectedType === null ? styles.filterActive : styles.filter}
            onClick={() => setSelectedType(null)}
          >
            All
          </button>
          {TYPES.map((t) => (
            <button
              key={t.value}
              className={selectedType === t.value ? styles.filterActive : styles.filter}
              onClick={() => setSelectedType(selectedType === t.value ? null : t.value)}
            >
              {t.label}
            </button>
          ))}
        </div>

        {showUpload && (
          <div className={styles.uploadForm}>
            <input
              className={styles.input}
              placeholder="Title"
              value={uploadTitle}
              onChange={(e) => setUploadTitle(e.target.value)}
            />
            <input
              className={styles.input}
              placeholder="Description (optional)"
              value={uploadDesc}
              onChange={(e) => setUploadDesc(e.target.value)}
            />
            <select
              className={styles.input}
              value={uploadType}
              onChange={(e) => setUploadType(e.target.value)}
            >
              <option value="ARTICLE">Article</option>
              <option value="RESEARCH">Research</option>
              <option value="PDF">PDF</option>
            </select>
            <input
              className={styles.input}
              type="file"
              onChange={(e) => setUploadFile(e.target.files?.[0] ?? null)}
            />
            <Button onClick={handleUpload} disabled={!uploadFile || !uploadTitle}>
              Submit
            </Button>
          </div>
        )}

        {saveError && <p className={styles.error}>{saveError}</p>}

        {recs.length > 0 && !debouncedSearch && !selectedType && (
          <div className={styles.recommendationsSection}>
            <h2 className={styles.sectionTitle}>Recommended for You</h2>
            <div className={styles.recommendationsList}>
              {recs.map((item) => (
                <div key={item.id} className={styles.recommendationCard}>
                  <span className={styles.type}>{item.contentType}</span>
                  <h3 className={styles.cardTitle}>{item.title}</h3>
                  {item.uploaderName && <p className={styles.author}>by {item.uploaderName}</p>}
                  <div className={styles.actions}>
                    <Button variant="outlined" onClick={() => openFile(item.id)}>Open</Button>
                    <Button variant="ghost" onClick={() => saveToList(item.id)}>Save</Button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        <div className={styles.grid}>
          {items.map((item) => (
            <div key={item.id} className={styles.card}>
              <div className={styles.cardHeader}>
                <span className={styles.type}>{item.contentType}</span>
              </div>
              <h3 className={styles.cardTitle}>{item.title}</h3>
              {item.uploaderName && <p className={styles.author}>by {item.uploaderName}</p>}
              {item.description && <p className={styles.description}>{item.description}</p>}
              <div className={styles.actions}>
                <Button variant="outlined" onClick={() => openFile(item.id)}>
                  Open
                </Button>
                <Button variant="ghost" onClick={() => saveToList(item.id)}>
                  Save
                </Button>
                <Button variant="ghost" onClick={() => setAnnotatingId(item.id)}>
                  Notes
                </Button>
              </div>
            </div>
          ))}
        </div>

        {annotatingId && (
          <AnnotationPanel contentId={annotatingId} onClose={() => setAnnotatingId(null)} />
        )}
      </div>
    </Page>
  )
}
