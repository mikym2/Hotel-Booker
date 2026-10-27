
import { useEffect, useState } from 'react'

function App() {
  const [name, setName] = useState('')
  const [message, setMessage] = useState('')
  const [contacts, setContacts] = useState([])
  const [loading, setLoading] = useState(false)
  const [loadingContacts, setLoadingContacts] = useState(true)
  const [error, setError] = useState('')
  const [response, setResponse] = useState('')

  async function loadContacts() {
    setLoadingContacts(true)

    try {
      const result = await fetch('/api/contacts')

      if (!result.ok) {
        throw new Error('Could not load messages.')
      }

      const data = await result.json()
      setContacts(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoadingContacts(false)
    }
  }

  useEffect(() => {
    loadContacts()
  }, [])

  async function handleSubmit(event) {
    event.preventDefault()

    setError('')
    setResponse('')
    setLoading(true)

    try {
      const result = await fetch('/api/contact', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ name, message })
      })

      const data = await result.json()

      if (!result.ok) {
        throw new Error(data.error || 'Submission failed.')
      }

      setResponse(data.message)
      setName('')
      setMessage('')

      await loadContacts()
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <main>
      <h1>Contact Us</h1>

      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="name">Your name</label>
          <br />
          <input
            id="name"
            value={name}
            onChange={e => setName(e.target.value)}
            required
          />
        </div>

        <br />

        <div>
          <label htmlFor="message">Your message</label>
          <br />
          <textarea
            id="message"
            value={message}
            onChange={e => setMessage(e.target.value)}
            required
          />
        </div>

        <br />

        <button type="submit" disabled={loading}>
          {loading ? 'Sending...' : 'Send message'}
        </button>
      </form>

      {response && <p role="status">{response}</p>}
      {error && <p role="alert">Error: {error}</p>}

      <hr />

      <h2>Saved Messages</h2>

      {loadingContacts && <p>Loading messages...</p>}

      {!loadingContacts && contacts.length === 0 && (
        <p>No messages yet.</p>
      )}

      <ul>
        {contacts.map(contact => (
          <li key={contact.id}>
            <strong>{contact.name}</strong>
            <p>{contact.message}</p>
          </li>
        ))}
      </ul>
    </main>
  )
}

export default App
