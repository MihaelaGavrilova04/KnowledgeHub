import { useEffect, useState } from 'react'
import { Button } from '../button/button'
import { useAuth } from '../../context/auth-context'
import { api } from '../../services/api'
import styles from './annotation-panel.module.css'

interface AnnotationComment {
  id: string
  commentText: string
  authorName: string
  createdAt: string
}

interface Annotation {
  id: string
  noteText: string
  authorName: string
  authorId: string
  createdAt: string
  comments: AnnotationComment[]
}

interface Props {
  contentId: string
  onClose: () => void
}

export function AnnotationPanel({ contentId, onClose }: Props) {
  const { user } = useAuth()
  const [annotations, setAnnotations] = useState<Annotation[]>([])
  const [note, setNote] = useState('')
  const [commentTexts, setCommentTexts] = useState<Record<string, string>>({})
  const [error, setError] = useState<string | null>(null)

  function loadAnnotations() {
    api.get<Annotation[]>(`/api/annotations?contentId=${contentId}`)
      .then(setAnnotations)
      .catch(() => {})
  }

  useEffect(() => {
    loadAnnotations()
    const intervalId = setInterval(loadAnnotations, 5000)
    return () => clearInterval(intervalId)
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [contentId])

  async function handleAddNote() {
    if (!note.trim()) return
    try {
      await api.post(`/api/annotations/${contentId}`, { noteText: note })
      setNote('')
      setError(null)
      loadAnnotations()
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to add note')
    }
  }

  async function handleDeleteAnnotation(id: string) {
    await api.delete(`/api/annotations/${id}`)
    loadAnnotations()
  }

  async function handleAddComment(annotationId: string) {
    const text = commentTexts[annotationId]?.trim()
    if (!text) return
    await api.post(`/api/annotations/${annotationId}/comments`, { commentText: text })
    setCommentTexts((prev) => ({ ...prev, [annotationId]: '' }))
    loadAnnotations()
  }

  return (
    <div className={styles.panel}>
      <div className={styles.header}>
        <h3 className={styles.heading}>Notes</h3>
        <Button variant="ghost" onClick={onClose}>Close</Button>
      </div>

      <div className={styles.addNote}>
        <textarea
          className={styles.textarea}
          placeholder="Add a note..."
          value={note}
          onChange={(e) => setNote(e.target.value)}
          rows={3}
        />
        <Button type="button" onClick={handleAddNote}>
          Save note
        </Button>
        {error && <p className={styles.error}>{error}</p>}
      </div>

      <div className={styles.annotations}>
        {annotations.length === 0 && (
          <p className={styles.empty}>No notes yet. Be the first to add one.</p>
        )}

        {annotations.map((annotation) => (
          <div key={annotation.id} className={styles.annotation}>
            <div className={styles.annotationHeader}>
              <span className={styles.annotationAuthor}>{annotation.authorName}</span>
              {user?.id === annotation.authorId && (
                <Button variant="ghost" onClick={() => handleDeleteAnnotation(annotation.id)}>Delete</Button>
              )}
            </div>
            <p className={styles.noteText}>{annotation.noteText}</p>

            {annotation.comments.map((comment) => (
              <div key={comment.id} className={styles.comment}>
                <span className={styles.commentAuthor}>{comment.authorName}</span>
                <p className={styles.commentText}>{comment.commentText}</p>
              </div>
            ))}

            <div className={styles.commentForm}>
              <input
                className={styles.input}
                placeholder="Add a comment..."
                value={commentTexts[annotation.id] ?? ''}
                onChange={(e) =>
                  setCommentTexts((prev) => ({ ...prev, [annotation.id]: e.target.value }))
                }
              />
              <Button onClick={() => handleAddComment(annotation.id)}>
                Comment
              </Button>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
