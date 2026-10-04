export default function TaskTable({ tasks, loading, error }) {
  if (error) {
    return (
      <div className="state-message error" role="alert">
        Error: {error}
      </div>
    );
  }

  const hasTasks = tasks && tasks.length > 0;

  // Only show the full-page loading message on the very first load
  if (loading && !hasTasks) {
    return (
      <div className="state-message" role="status">
        Loading tasks...
      </div>
    );
  }

  if (!hasTasks) {
    return <div className="state-message">No tasks found.</div>;
  }

  return (
    <table
      className={`task-table${loading ? ' is-loading' : ''}`}
      aria-busy={loading}
    >
      <thead>
        <tr>
          <th>ID</th>
          <th>Title</th>
          <th>Status</th>
          <th>Priority</th>
          <th>Assignee</th>
        </tr>
      </thead>
      <tbody>
        {tasks.map((task) => (
          <tr key={task.id}>
            <td>{task.id}</td>
            <td>
              <div className="task-title">{task.title}</div>
              <div className="task-desc">{task.description}</div>
            </td>
            <td>
              <span className={`status-badge ${(task.status || '').toLowerCase()}`}>
                {task.status}
              </span>
            </td>
            <td>{task.priority || '\u2014'}</td>
            <td>{task.assignee || '\u2014'}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}