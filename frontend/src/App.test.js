import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import App from './App';

function renderApp() {
  return render(
    <BrowserRouter>
      <AuthProvider>
        <App />
      </AuthProvider>
    </BrowserRouter>
  );
}

test('renders login link when not authenticated', () => {
  renderApp();
  const link = screen.getByText(/register/i);
  expect(link).toBeInTheDocument();
});
