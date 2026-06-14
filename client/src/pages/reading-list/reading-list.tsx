import { Page } from '../../components/page/page'
import { Button } from '../../components/button/button'
import { useAsync } from '../../hooks/use-async'
import { api } from '../../services/api'
import styles from './reading-list.module.css'

interface ContentItem {
  id: string
  title: string
  description: string | null
  contentType: string
  authorName: string | null
}

export function ReadingListPage() {
  const { data: items = [], reload } = useAsync<ContentItem[]>(
    () => api.get<ContentItem[]>('/api/reading-list'),
  )

  async function removeItem(id: string) {
    await api.delete(`/api/reading-list/items/${id}`)
    reload()
  }

  return (
    <Page>
      <div className={styles.container}>
        <h2 className={styles.heading}>My Reading List</h2>

        {items.length === 0 && (
          <p className={styles.empty}>No items saved yet. Browse resources to add some.</p>
        )}

        <div className={styles.list}>
          {items.map((item) => (
            <div key={item.id} className={styles.item}>
              <div className={styles.info}>
                <span className={styles.type}>{item.contentType}</span>
                <h3 className={styles.title}>{item.title}</h3>
                {item.authorName && <p className={styles.author}>by {item.authorName}</p>}
                {item.description && <p className={styles.description}>{item.description}</p>}
              </div>
              <Button variant="ghost" onClick={() => removeItem(item.id)}>
                Remove
              </Button>
            </div>
          ))}
        </div>
      </div>
    </Page>
  )
}
