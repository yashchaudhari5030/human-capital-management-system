import { useEffect, useState } from 'react'
import { getNotifications, markRead } from '../../api/notificationApi'
import Loader from '../../components/ui/Loader'

export default function NotificationList() {
  const [data, setData] = useState([])
  const [loading, setLoading] = useState(true)

  const load = async () => { setLoading(true); const r = await getNotifications(); setData(r.data); setLoading(false) }
  useEffect(() => { load() }, [])

  const handleRead = async id => { await markRead(id); load() }

  if (loading) return <Loader />
  return (
    <div>
      <h1 className="text-xl font-bold mb-4">Notifications</h1>
      <div className="space-y-2">
        {data.map(n => (
          <div key={n.id} className={`p-4 rounded shadow ${n.read ? 'bg-gray-100' : 'bg-white'}`}>
            <p>{n.message}</p>
            {!n.read && <button onClick={() => handleRead(n.id)} className="text-primary-600 text-sm mt-2">Mark as read</button>}
          </div>
        ))}
      </div>
    </div>
  )
}



