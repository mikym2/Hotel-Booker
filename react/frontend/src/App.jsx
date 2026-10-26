
import { useState } from 'react'

function App() {
  const [name, setName] = useState('')
  const [message, setMessage] = useState('')
  const [response, setResponse] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(event) {
    event.preventDefault()

    setResponse('')
    setError('')
    setLoading(true)

    try {
      const result = await fetch('/api/contact', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          name: name,
          message: message
        })
      })

      const data = await result.json()

      if (!result.ok) {
        throw new Error(data.error || 'Request failed')
      }

      setResponse(data.message)
      setName('')
      setMessage('')
    } catch (err) {
      setError(err.message || 'Could not contact the server.')
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
            onChange={(event) => setName(event.target.value)}
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
            onChange={(event) => setMessage(event.target.value)}
            required
          />
        </div>

        <br />

        <button type="submit" disabled={loading}>
          {loading ? 'Sending...' : 'Send message'}
        </button>
      </form>

      {response && <p>{response}</p>}
      {error && <p role="alert">Error: {error}</p>}
    </main>
  )
}

export default App
