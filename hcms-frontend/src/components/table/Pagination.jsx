export default function Pagination({ page, totalPages, onPageChange }) {
  return (
    <div className="flex gap-2 justify-center mt-4">
      <button disabled={page <= 0} onClick={() => onPageChange(page - 1)} className="px-3 py-1 border rounded disabled:opacity-50">Prev</button>
      <span className="px-3 py-1">{page + 1} / {totalPages || 1}</span>
      <button disabled={page >= totalPages - 1} onClick={() => onPageChange(page + 1)} className="px-3 py-1 border rounded disabled:opacity-50">Next</button>
    </div>
  )
}



