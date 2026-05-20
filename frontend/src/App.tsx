import { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate, useNavigate } from 'react-router-dom';
import { 
  Calendar, Clock, User as UserIcon, Shield, Plus, Trash2, LogOut, 
  Activity, CheckCircle, XCircle, AlertTriangle, Search, 
  DollarSign, MapPin, UserCheck, RefreshCw, BookOpen, Stethoscope,
  Sun, Moon
} from 'lucide-react';
import api from './api';

// --- TS Interfaces ---
interface User {
  id: number;
  fullName: string;
  email: string;
  phone: string;
  role: 'ADMIN' | 'DOCTOR' | 'PATIENT';
  enabled: boolean;
}

interface Doctor {
  id: number;
  specialization: string;
  qualification: string;
  experience: number;
  consultationFee: number;
  hospitalName: string;
  bio: string;
  user: User;
}

interface Patient {
  id: number;
  gender: string;
  dateOfBirth: string;
  address: string;
  bloodGroup: string;
  emergencyContact: string;
  user: User;
}

interface Availability {
  id: number;
  doctorId: number;
  doctorName: string;
  dayOfWeek: string;
  startTime: string;
  endTime: string;
  active: boolean;
}

interface Appointment {
  id: number;
  doctor: Doctor;
  patient: Patient;
  appointmentDate: string;
  appointmentTime: string;
  status: 'BOOKED' | 'COMPLETED' | 'CANCELLED' | 'RESCHEDULED';
  notes: string;
  createdAt: string;
}

// --- Root Component with Navigation ---
export default function App() {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [darkMode, setDarkMode] = useState(() => {
    return localStorage.getItem('darkMode') === 'true';
  });

  useEffect(() => {
    if (darkMode) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
    localStorage.setItem('darkMode', String(darkMode));
  }, [darkMode]);

  useEffect(() => {
    const token = localStorage.getItem('token');
    const storedUser = localStorage.getItem('user');
    if (token && storedUser) {
      setUser(JSON.parse(storedUser));
    }
    setLoading(false);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
    window.location.href = '/login';
  };

  if (loading) {
    return (
      <div style={{ display: 'flex', height: '100vh', justifyContent: 'center', alignItems: 'center', background: '#f8fafc' }}>
        <RefreshCw className="animate-spin" size={48} color="#aa3bff" />
      </div>
    );
  }

  return (
    <BrowserRouter>
      {!user && (
        <button
          onClick={() => setDarkMode(!darkMode)}
          className="btn btn-secondary"
          style={{
            position: 'fixed',
            top: '20px',
            right: '20px',
            zIndex: 1000,
            borderRadius: '50%',
            width: '45px',
            height: '45px',
            padding: 0,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            boxShadow: 'var(--shadow-md)',
          }}
          title="Toggle Dark Mode"
        >
          {darkMode ? <Sun size={20} /> : <Moon size={20} />}
        </button>
      )}

      {user && (
        <nav className="glass-nav" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <Activity color="#aa3bff" size={32} />
            <span style={{ fontSize: '22px', fontWeight: 700, letterSpacing: '-0.5px' }}>
              Care<span style={{ color: '#aa3bff' }}>Booking</span>
            </span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <div style={{ width: '40px', height: '40px', borderRadius: '50%', background: 'rgba(170, 59, 255, 0.1)', display: 'flex', justifyContent: 'center', alignItems: 'center', color: '#aa3bff', fontWeight: 600 }}>
                {user.fullName.charAt(0)}
              </div>
              <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start' }}>
                <span style={{ fontSize: '14px', fontWeight: 600 }}>{user.fullName}</span>
                <span className="badge badge-info" style={{ fontSize: '10px', padding: '2px 6px', marginTop: '2px' }}>{user.role}</span>
              </div>
            </div>
            <button 
              onClick={() => setDarkMode(!darkMode)} 
              className="btn btn-secondary" 
              style={{ padding: '8px 12px', fontSize: '14px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}
              title="Toggle Dark Mode"
            >
              {darkMode ? <Sun size={16} /> : <Moon size={16} />}
            </button>
            <button onClick={handleLogout} className="btn btn-secondary" style={{ padding: '8px 16px', fontSize: '14px' }}>
              <LogOut size={16} /> Logout
            </button>
          </div>
        </nav>
      )}

      <Routes>
        <Route path="/login" element={user ? <Navigate to="/dashboard" /> : <AuthPage setUser={setUser} />} />
        <Route path="/dashboard" element={user ? <DashboardRedirector user={user} /> : <Navigate to="/login" />} />
        <Route path="/patient" element={user?.role === 'PATIENT' ? <PatientDashboard user={user} /> : <Navigate to="/login" />} />
        <Route path="/doctor" element={user?.role === 'DOCTOR' ? <DoctorDashboard user={user} /> : <Navigate to="/login" />} />
        <Route path="/admin" element={user?.role === 'ADMIN' ? <AdminDashboard /> : <Navigate to="/login" />} />
        <Route path="*" element={<Navigate to="/dashboard" />} />
      </Routes>
    </BrowserRouter>
  );
}

// --- Dashboard Redirector ---
function DashboardRedirector({ user }: { user: User }) {
  if (user.role === 'PATIENT') return <Navigate to="/patient" />;
  if (user.role === 'DOCTOR') return <Navigate to="/doctor" />;
  if (user.role === 'ADMIN') return <Navigate to="/admin" />;
  return <Navigate to="/login" />;
}

// --- Authentication Page ---
function AuthPage({ setUser }: { setUser: (user: User) => void }) {
  const [isRegister, setIsRegister] = useState(false);
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [phone, setPhone] = useState('');
  const [role, setRole] = useState<'PATIENT' | 'DOCTOR'>('PATIENT');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);

    try {
      if (isRegister) {
        await api.post('/api/auth/register', { fullName, email, password, phone, role });
        setSuccess('Registration successful! Please login.');
        setIsRegister(false);
        setPassword('');
      } else {
        const response = await api.post('/api/auth/login', { email, password });
        localStorage.setItem('token', response.data.accessToken);
        localStorage.setItem('user', JSON.stringify(response.data.user));
        setUser(response.data.user);
        navigate('/dashboard');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Authentication failed. Please verify credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-wrapper">
      <div className="glass-panel auth-card" style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
        <div style={{ textAlign: 'center' }}>
          <Activity color="#aa3bff" size={48} style={{ margin: '0 auto 12px' }} />
          <h2 style={{ fontSize: '28px', fontWeight: 700 }}>
            {isRegister ? 'Create an Account' : 'Welcome Back'}
          </h2>
          <p style={{ color: 'var(--text-secondary)', fontSize: '15px', marginTop: '4px' }}>
            {isRegister ? 'Join our digital care booking platform' : 'Access your clinic scheduler'}
          </p>
        </div>

        {error && (
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', padding: '12px', background: 'rgba(239, 68, 68, 0.1)', color: 'var(--danger)', borderRadius: '8px', fontSize: '14px' }}>
            <AlertTriangle size={18} />
            <span>{error}</span>
          </div>
        )}

        {success && (
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', padding: '12px', background: 'rgba(16, 185, 129, 0.1)', color: 'var(--success)', borderRadius: '8px', fontSize: '14px' }}>
            <CheckCircle size={18} />
            <span>{success}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {isRegister && (
            <div className="form-group">
              <label className="form-label">Full Name</label>
              <input type="text" required value={fullName} onChange={(e) => setFullName(e.target.value)} className="form-control" placeholder="John Doe" />
            </div>
          )}

          <div className="form-group">
            <label className="form-label">Email Address</label>
            <input type="email" required value={email} onChange={(e) => setEmail(e.target.value)} className="form-control" placeholder="user@clinic.com" />
          </div>

          <div className="form-group">
            <label className="form-label">Password</label>
            <input type="password" required value={password} onChange={(e) => setPassword(e.target.value)} className="form-control" placeholder="••••••••" />
          </div>

          {isRegister && (
            <>
              <div className="form-group">
                <label className="form-label">Phone Number</label>
                <input type="text" required value={phone} onChange={(e) => setPhone(e.target.value)} className="form-control" placeholder="+1 234 5678" />
              </div>

              <div className="form-group">
                <label className="form-label">I am registering as a:</label>
                <select value={role} onChange={(e) => setRole(e.target.value as 'PATIENT' | 'DOCTOR')} className="form-control">
                  <option value="PATIENT">Patient</option>
                  <option value="DOCTOR">Doctor</option>
                </select>
              </div>
            </>
          )}

          <button type="submit" className="btn btn-primary" style={{ width: '100%', padding: '14px' }} disabled={loading}>
            {loading ? <RefreshCw className="animate-spin" size={18} /> : (isRegister ? 'Register' : 'Login')}
          </button>
        </form>

        <div style={{ textAlign: 'center', fontSize: '14px', marginTop: '10px' }}>
          <span style={{ color: 'var(--text-secondary)' }}>
            {isRegister ? 'Already have an account? ' : "Don't have an account? "}
          </span>
          <button onClick={() => { setIsRegister(!isRegister); setError(''); }} style={{ background: 'none', border: 'none', color: '#aa3bff', fontWeight: 600, cursor: 'pointer', outline: 'none' }}>
            {isRegister ? 'Sign In' : 'Sign Up'}
          </button>
        </div>
      </div>
    </div>
  );
}

// --- Patient Dashboard Component ---
function PatientDashboard({ user: _user }: { user: User }) {
  const [patient, setPatient] = useState<Patient | null>(null);
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const [loading, setLoading] = useState(true);

  // Profile Form States
  const [gender, setGender] = useState('MALE');
  const [dob, setDob] = useState('');
  const [address, setAddress] = useState('');
  const [bloodGroup, setBloodGroup] = useState('O+');
  const [emergencyContact, setEmergencyContact] = useState('');

  // Booking Modal States
  const [showBookModal, setShowBookModal] = useState(false);
  const [selectedDoctor, setSelectedDoctor] = useState<Doctor | null>(null);
  const [availabilities, setAvailabilities] = useState<Availability[]>([]);
  const [bookDate, setBookDate] = useState('');
  const [bookTime, setBookTime] = useState('');
  const [bookNotes, setBookNotes] = useState('');
  const [bookingError, setBookingError] = useState('');
  const [bookingSuccess, setBookingSuccess] = useState('');
  const [selectedAvailId, setSelectedAvailId] = useState<number | null>(null);

  const getNextDateForDay = (dayName: string) => {
    const days = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
    const targetDay = days.indexOf(dayName.toUpperCase());
    if (targetDay === -1) return '';
    const today = new Date();
    const resultDate = new Date(today);
    const currentDay = today.getDay();
    let daysToAdd = targetDay - currentDay;
    if (daysToAdd <= 0) {
      daysToAdd += 7; // Get next week's occurrence
    }
    resultDate.setDate(today.getDate() + daysToAdd);
    return resultDate.toISOString().split('T')[0];
  };

  // Dynamic filtering states
  const [allDoctors, setAllDoctors] = useState<Doctor[]>([]);
  const [selectedSpec, setSelectedSpec] = useState('ALL');
  const [selectedDocId, setSelectedDocId] = useState('ALL');

  useEffect(() => {
    fetchPatientProfile();
    fetchAppointments();
    fetchDoctors();
  }, []);

  const fetchPatientProfile = async () => {
    try {
      const response = await api.get('/api/patients/me');
      setPatient(response.data);
    } catch (err) {
      setPatient(null);
    }
  };

  const fetchAppointments = async () => {
    try {
      const response = await api.get('/api/appointments/me');
      setAppointments(response.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchDoctors = async () => {
    try {
      const response = await api.get('/api/doctors');
      setAllDoctors(response.data);
      setDoctors(response.data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    let filtered = allDoctors;
    if (selectedSpec !== 'ALL') {
      filtered = filtered.filter(d => d.specialization === selectedSpec);
    }
    if (selectedDocId !== 'ALL') {
      filtered = filtered.filter(d => d.id === parseInt(selectedDocId));
    }
    setDoctors(filtered);
  }, [selectedSpec, selectedDocId, allDoctors]);

  const handleCreateProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await api.post('/api/patients', { gender, dateOfBirth: dob, address, bloodGroup, emergencyContact });
      setPatient(response.data);
    } catch (err) {
      alert('Failed to save profile');
    }
  };

  const handleSelectDoctorForBooking = async (doc: Doctor) => {
    setSelectedDoctor(doc);
    setBookDate('');
    setBookTime('');
    setBookNotes('');
    setBookingError('');
    setBookingSuccess('');
    setSelectedAvailId(null);
    try {
      const response = await api.get(`/api/availabilities/doctor/${doc.id}/active`);
      setAvailabilities(response.data);
      setShowBookModal(true);
    } catch (err) {
      alert('Failed to retrieve schedules');
    }
  };

  const handleBookAppointment = async (e: React.FormEvent) => {
    e.preventDefault();
    setBookingError('');
    setBookingSuccess('');
    try {
      await api.post('/api/appointments', {
        doctorId: selectedDoctor?.id,
        appointmentDate: bookDate,
        appointmentTime: bookTime + ':00',
        notes: bookNotes
      });
      setBookingSuccess('Appointment booked successfully!');
      setTimeout(() => {
        setShowBookModal(false);
        fetchAppointments();
      }, 1500);
    } catch (err: any) {
      setBookingError(err.response?.data?.message || 'Error occurred. Please verify inputs.');
    }
  };

  const handleCancelAppointment = async (id: number) => {
    if (!confirm('Are you sure you want to cancel this appointment?')) return;
    try {
      await api.put(`/api/appointments/${id}/cancel`);
      fetchAppointments();
    } catch (err) {
      alert('Failed to cancel appointment');
    }
  };

  const specializations = Array.from(new Set(allDoctors.map(d => d.specialization)));
  const filteredDoctorsForDropdown = selectedSpec === 'ALL' 
    ? allDoctors 
    : allDoctors.filter(d => d.specialization === selectedSpec);

  if (loading) {
    return (
      <div style={{ display: 'flex', flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <RefreshCw className="animate-spin" size={32} color="#aa3bff" />
      </div>
    );
  }

  // Render profile setup if patient details are missing
  if (!patient) {
    return (
      <div style={{ maxWidth: '600px', margin: '40px auto', padding: '0 24px' }}>
        <div className="glass-panel" style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          <h2 style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <UserIcon color="#aa3bff" /> Setup Patient Profile
          </h2>
          <p style={{ color: 'var(--text-secondary)', fontSize: '15px' }}>
            We noticed you haven't completed your profile. Please fill out details below to start booking appointments.
          </p>
          <form onSubmit={handleCreateProfile} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <div className="form-group">
              <label className="form-label">Gender</label>
              <select value={gender} onChange={(e) => setGender(e.target.value)} className="form-control">
                <option value="MALE">Male</option>
                <option value="FEMALE">Female</option>
                <option value="OTHER">Other</option>
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Date of Birth</label>
              <input type="date" required value={dob} onChange={(e) => setDob(e.target.value)} className="form-control" />
            </div>
            <div className="form-group">
              <label className="form-label">Address</label>
              <input type="text" required value={address} onChange={(e) => setAddress(e.target.value)} className="form-control" placeholder="123 Oak St, Metropia" />
            </div>
            <div className="form-group">
              <label className="form-label">Blood Group</label>
              <select value={bloodGroup} onChange={(e) => setBloodGroup(e.target.value)} className="form-control">
                <option value="A+">A+</option>
                <option value="A-">A-</option>
                <option value="B+">B+</option>
                <option value="B-">B-</option>
                <option value="O+">O+</option>
                <option value="O-">O-</option>
                <option value="AB+">AB+</option>
                <option value="AB-">AB-</option>
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Emergency Contact Phone</label>
              <input type="text" required value={emergencyContact} onChange={(e) => setEmergencyContact(e.target.value)} className="form-control" placeholder="+1 555-0987" />
            </div>
            <button type="submit" className="btn btn-primary" style={{ padding: '14px' }}>Save Profile Details</button>
          </form>
        </div>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: '1200px', margin: '40px auto', padding: '0 24px', display: 'flex', flexDirection: 'column', gap: '30px' }}>
      
      {/* Search & Book Panel */}
      <div className="glass-panel" style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
        <h2 style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <Search color="#aa3bff" /> Find and Book a Doctor
        </h2>
        <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap' }}>
          <div className="form-group" style={{ flex: 1, minWidth: '200px', marginBottom: 0 }}>
            <label className="form-label">Specialization</label>
            <select 
              value={selectedSpec} 
              onChange={(e) => { setSelectedSpec(e.target.value); setSelectedDocId('ALL'); }} 
              className="form-control"
            >
              <option value="ALL">All Specializations</option>
              {specializations.map(spec => (
                <option key={spec} value={spec}>{spec}</option>
              ))}
            </select>
          </div>
          <div className="form-group" style={{ flex: 1, minWidth: '200px', marginBottom: 0 }}>
            <label className="form-label">Doctor Name</label>
            <select 
              value={selectedDocId} 
              onChange={(e) => setSelectedDocId(e.target.value)} 
              className="form-control"
            >
              <option value="ALL">All Doctors</option>
              {filteredDoctorsForDropdown.map(doc => (
                <option key={doc.id} value={doc.id}>{doc.user.fullName}</option>
              ))}
            </select>
          </div>
        </div>

        <div className="grid grid-cols-3" style={{ marginTop: '10px' }}>
          {doctors.map((doc) => (
            <div key={doc.id} className="glass-panel" style={{ background: '#ffffff', display: 'flex', flexDirection: 'column', justifyContent: 'space-between', gap: '15px' }}>
              <div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
                  <Stethoscope size={20} color="#aa3bff" />
                  <h3 style={{ fontSize: '18px', fontWeight: 600 }}>{doc.user.fullName}</h3>
                </div>
                <p style={{ color: '#aa3bff', fontSize: '14px', fontWeight: 600, marginBottom: '10px' }}>{doc.specialization}</p>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '4px', fontSize: '13px', color: 'var(--text-secondary)' }}>
                  <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}><BookOpen size={14} /> {doc.qualification} ({doc.experience} yrs exp)</span>
                  <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}><MapPin size={14} /> {doc.hospitalName}</span>
                  <span style={{ display: 'flex', alignItems: 'center', gap: '4px', fontWeight: 600, color: 'var(--text-primary)' }}><DollarSign size={14} /> Fee: ${doc.consultationFee.toFixed(2)}</span>
                </div>
                <p style={{ fontSize: '13px', color: 'var(--text-secondary)', marginTop: '8px', fontStyle: 'italic' }}>
                  {doc.bio ? `"${doc.bio.substring(0, 80)}..."` : 'No bio available.'}
                </p>
              </div>
              <button onClick={() => handleSelectDoctorForBooking(doc)} className="btn btn-primary" style={{ padding: '8px 16px', fontSize: '13px', width: '100%', marginTop: '10px' }}>
                Book Appointment
              </button>
            </div>
          ))}
          {doctors.length === 0 && (
            <p style={{ gridColumn: '1/-1', textAlign: 'center', color: 'var(--text-secondary)', padding: '20px' }}>No doctors found matching filters.</p>
          )}
        </div>
      </div>

      {/* Appointment Queue Panel */}
      <div className="glass-panel" style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
        <h2 style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <Calendar color="#aa3bff" /> My Appointments
        </h2>
        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>Doctor</th>
                <th>Specialization</th>
                <th>Date</th>
                <th>Time</th>
                <th>Status</th>
                <th>Notes</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {appointments.map((app) => (
                <tr key={app.id}>
                  <td><strong>{app.doctor.user.fullName}</strong></td>
                  <td>{app.doctor.specialization}</td>
                  <td>{app.appointmentDate}</td>
                  <td>{app.appointmentTime.substring(0, 5)}</td>
                  <td>
                    <span className={`badge badge-${app.status === 'CANCELLED' ? 'danger' : app.status === 'BOOKED' ? 'info' : 'success'}`}>
                      {app.status}
                    </span>
                  </td>
                  <td><span style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>{app.notes || '-'}</span></td>
                  <td>
                    {app.status === 'BOOKED' && (
                      <button onClick={() => handleCancelAppointment(app.id)} className="btn btn-danger" style={{ padding: '6px 12px', fontSize: '12px' }}>
                        Cancel
                      </button>
                    )}
                  </td>
                </tr>
              ))}
              {appointments.length === 0 && (
                <tr>
                  <td colSpan={7} style={{ textAlign: 'center', color: 'var(--text-secondary)' }}>You don't have any appointments scheduled yet.</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Booking Modal */}
      {showBookModal && selectedDoctor && (
        <div className="modal-overlay">
          <div className="glass-panel modal-content" style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h2>Schedule Appointment</h2>
              <button onClick={() => setShowBookModal(false)} style={{ background: 'none', border: 'none', fontSize: '20px', cursor: 'pointer' }}><XCircle /></button>
            </div>

            <div style={{ padding: '12px', background: '#f1f5f9', borderRadius: '10px', fontSize: '14px' }}>
              <p><strong>Doctor:</strong> {selectedDoctor.user.fullName}</p>
              <p><strong>Specialty:</strong> {selectedDoctor.specialization}</p>
              <p><strong>Fee:</strong> ${selectedDoctor.consultationFee.toFixed(2)}</p>
            </div>

            {/* Availabilities display */}
            <div>
              <p className="form-label" style={{ marginBottom: '8px' }}>Doctor Availability Schedules:</p>
              <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
                {availabilities.map((av) => {
                  const isSelected = selectedAvailId === av.id;
                  return (
                    <button
                      key={av.id}
                      type="button"
                      className="btn"
                      style={{
                        padding: '6px 12px',
                        fontSize: '12px',
                        textTransform: 'none',
                        borderRadius: '8px',
                        backgroundColor: isSelected ? 'var(--primary-color)' : 'var(--border-color)',
                        color: isSelected ? 'white' : 'var(--text-primary)',
                        border: isSelected ? '1px solid var(--primary-hover)' : '1px solid transparent',
                        display: 'inline-flex',
                        alignItems: 'center',
                        gap: '4px'
                      }}
                      onClick={() => {
                        setSelectedAvailId(av.id);
                        const nextDate = getNextDateForDay(av.dayOfWeek);
                        setBookDate(nextDate);
                        setBookTime(av.startTime.substring(0, 5));
                      }}
                    >
                      <Clock size={12} /> {av.dayOfWeek}: {av.startTime.substring(0, 5)} - {av.endTime.substring(0, 5)}
                    </button>
                  );
                })}
                {availabilities.length === 0 && (
                  <span className="badge badge-danger" style={{ textTransform: 'none' }}>Doctor has not set any active scheduling slots yet.</span>
                )}
              </div>
            </div>

            {bookingError && (
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', padding: '12px', background: 'rgba(239, 68, 68, 0.1)', color: 'var(--danger)', borderRadius: '8px', fontSize: '14px' }}>
                <AlertTriangle size={18} />
                <span>{bookingError}</span>
              </div>
            )}

            {bookingSuccess && (
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', padding: '12px', background: 'rgba(16, 185, 129, 0.1)', color: 'var(--success)', borderRadius: '8px', fontSize: '14px' }}>
                <CheckCircle size={18} />
                <span>{bookingSuccess}</span>
              </div>
            )}

            <form onSubmit={handleBookAppointment} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
              <div className="form-group">
                <label className="form-label">Appointment Date</label>
                <input type="date" required value={bookDate} onChange={(e) => setBookDate(e.target.value)} className="form-control" />
              </div>
              <div className="form-group">
                <label className="form-label">Appointment Time</label>
                <input type="time" required value={bookTime} onChange={(e) => setBookTime(e.target.value)} className="form-control" />
              </div>
              <div className="form-group">
                <label className="form-label">Notes for the Doctor</label>
                <textarea value={bookNotes} onChange={(e) => setBookNotes(e.target.value)} className="form-control" rows={3} placeholder="Symptom descriptions, history details..."></textarea>
              </div>
              <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end', marginTop: '10px' }}>
                <button type="button" onClick={() => setShowBookModal(false)} className="btn btn-secondary">Cancel</button>
                <button type="submit" className="btn btn-primary" disabled={availabilities.length === 0}>Book Now</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

// --- Doctor Dashboard Component ---
function DoctorDashboard({ user }: { user: User }) {
  const [doctor, setDoctor] = useState<Doctor | null>(null);
  const [availabilities, setAvailabilities] = useState<Availability[]>([]);
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [loading, setLoading] = useState(true);

  // Profile setup states
  const [spec, setSpec] = useState('');
  const [qual, setQual] = useState('');
  const [exp, setExp] = useState(0);
  const [fee, setFee] = useState(0);
  const [hospital, setHospital] = useState('');
  const [bio, setBio] = useState('');

  // Availability setup states
  const [day, setDay] = useState('MONDAY');
  const [start, setStart] = useState('09:00');
  const [end, setEnd] = useState('17:00');

  useEffect(() => {
    fetchDoctorProfile();
  }, []);

  const fetchDoctorProfile = async () => {
    try {
      const response = await api.get(`/api/doctors/user/${user.id}`);
      setDoctor(response.data);
      fetchAvailabilities(response.data.id);
      fetchAppointments(response.data.id);
    } catch (err) {
      setDoctor(null);
      setLoading(false);
    }
  };

  const fetchAvailabilities = async (docId: number) => {
    try {
      const response = await api.get(`/api/availabilities/doctor/${docId}`);
      setAvailabilities(response.data);
    } catch (err) {
      console.error(err);
    }
  };

  const fetchAppointments = async (docId: number) => {
    try {
      const response = await api.get(`/api/appointments/doctor/${docId}`);
      setAppointments(response.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await api.post('/api/doctors', { specialization: spec, qualification: qual, experience: exp, consultationFee: fee, hospitalName: hospital, bio });
      setDoctor(response.data);
      fetchAvailabilities(response.data.id);
      fetchAppointments(response.data.id);
    } catch (err) {
      alert('Failed to save profile');
    }
  };

  const handleAddAvailability = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/api/availabilities', { dayOfWeek: day, startTime: start + ':00', endTime: end + ':00' });
      if (doctor) fetchAvailabilities(doctor.id);
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to add availability');
    }
  };

  const handleDeleteAvailability = async (id: number) => {
    try {
      await api.delete(`/api/availabilities/${id}`);
      if (doctor) fetchAvailabilities(doctor.id);
    } catch (err) {
      alert('Failed to delete availability');
    }
  };

  const handleCancelAppointment = async (id: number) => {
    if (!confirm('Are you sure you want to cancel this appointment?')) return;
    try {
      await api.put(`/api/appointments/${id}/cancel`);
      if (doctor) fetchAppointments(doctor.id);
    } catch (err) {
      alert('Failed to cancel appointment');
    }
  };

  if (loading) {
    return (
      <div style={{ display: 'flex', flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <RefreshCw className="animate-spin" size={32} color="#aa3bff" />
      </div>
    );
  }

  // Profile setup if doctor has no records
  if (!doctor) {
    return (
      <div style={{ maxWidth: '600px', margin: '40px auto', padding: '0 24px' }}>
        <div className="glass-panel" style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          <h2 style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <UserCheck color="#aa3bff" /> Setup Doctor Profile
          </h2>
          <p style={{ color: 'var(--text-secondary)', fontSize: '15px' }}>
            We noticed your professional doctor profile has not been completed yet. Fill out fields below so patient users can locate and schedule checkups.
          </p>
          <form onSubmit={handleCreateProfile} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <div className="form-group">
              <label className="form-label">Specialization</label>
              <input type="text" required value={spec} onChange={(e) => setSpec(e.target.value)} className="form-control" placeholder="Cardiology, Pediatrics..." />
            </div>
            <div className="form-group">
              <label className="form-label">Qualification</label>
              <input type="text" required value={qual} onChange={(e) => setQual(e.target.value)} className="form-control" placeholder="MD - Cardiology" />
            </div>
            <div className="form-group">
              <label className="form-label">Experience (Years)</label>
              <input type="number" required value={exp} onChange={(e) => setExp(parseInt(e.target.value))} className="form-control" placeholder="10" />
            </div>
            <div className="form-group">
              <label className="form-label">Consultation Fee ($)</label>
              <input type="number" step="0.01" required value={fee} onChange={(e) => setFee(parseFloat(e.target.value))} className="form-control" placeholder="150.00" />
            </div>
            <div className="form-group">
              <label className="form-label">Hospital Name</label>
              <input type="text" required value={hospital} onChange={(e) => setHospital(e.target.value)} className="form-control" placeholder="General Medical Center" />
            </div>
            <div className="form-group">
              <label className="form-label">Professional Biography</label>
              <textarea value={bio} onChange={(e) => setBio(e.target.value)} className="form-control" rows={3} placeholder="Tell patients about your expertise..."></textarea>
            </div>
            <button type="submit" className="btn btn-primary" style={{ padding: '14px' }}>Save Profile Details</button>
          </form>
        </div>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: '1200px', margin: '40px auto', padding: '0 24px', display: 'flex', flexDirection: 'column', gap: '30px' }}>
      
      <div className="grid grid-cols-2">
        {/* Availability Planner */}
        <div className="glass-panel" style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          <h2 style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Clock color="#aa3bff" /> Manage Weekly Availability
          </h2>
          <form onSubmit={handleAddAvailability} style={{ display: 'flex', gap: '12px', flexWrap: 'wrap', alignItems: 'flex-end' }}>
            <div className="form-group" style={{ flex: 1, minWidth: '130px', marginBottom: 0 }}>
              <label className="form-label">Day</label>
              <select value={day} onChange={(e) => setDay(e.target.value)} className="form-control">
                <option value="MONDAY">Monday</option>
                <option value="TUESDAY">Tuesday</option>
                <option value="WEDNESDAY">Wednesday</option>
                <option value="THURSDAY">Thursday</option>
                <option value="FRIDAY">Friday</option>
                <option value="SATURDAY">Saturday</option>
                <option value="SUNDAY">Sunday</option>
              </select>
            </div>
            <div className="form-group" style={{ flex: 1, minWidth: '100px', marginBottom: 0 }}>
              <label className="form-label">Start</label>
              <input type="time" required value={start} onChange={(e) => setStart(e.target.value)} className="form-control" />
            </div>
            <div className="form-group" style={{ flex: 1, minWidth: '100px', marginBottom: 0 }}>
              <label className="form-label">End</label>
              <input type="time" required value={end} onChange={(e) => setEnd(e.target.value)} className="form-control" />
            </div>
            <button type="submit" className="btn btn-primary" style={{ display: 'flex', alignItems: 'center', height: '45px' }}><Plus size={18} /> Add</button>
          </form>

          {/* Availability List */}
          <div className="table-container" style={{ marginTop: '10px' }}>
            <table className="table">
              <thead>
                <tr>
                  <th>Day</th>
                  <th>Hours</th>
                  <th>Status</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {availabilities.map((av) => (
                  <tr key={av.id}>
                    <td><strong>{av.dayOfWeek}</strong></td>
                    <td>{av.startTime.substring(0, 5)} - {av.endTime.substring(0, 5)}</td>
                    <td><span className="badge badge-success">Active</span></td>
                    <td>
                      <button onClick={() => handleDeleteAvailability(av.id)} style={{ background: 'none', border: 'none', color: 'var(--danger)', cursor: 'pointer' }}>
                        <Trash2 size={16} />
                      </button>
                    </td>
                  </tr>
                ))}
                {availabilities.length === 0 && (
                  <tr>
                    <td colSpan={4} style={{ textAlign: 'center', color: 'var(--text-secondary)' }}>You haven't defined any scheduling blocks yet.</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>

        {/* Doctor Details Summary */}
        <div className="glass-panel" style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
          <h2 style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <UserCheck color="#aa3bff" /> Professional Profile
          </h2>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', fontSize: '15px' }}>
            <p><strong>Hospital:</strong> {doctor.hospitalName}</p>
            <p><strong>Specialty:</strong> {doctor.specialization}</p>
            <p><strong>Qualifications:</strong> {doctor.qualification}</p>
            <p><strong>Experience:</strong> {doctor.experience} Years</p>
            <p><strong>Consultation Fee:</strong> ${doctor.consultationFee.toFixed(2)}</p>
            <p style={{ marginTop: '10px', fontStyle: 'italic', borderLeft: '3px solid #aa3bff', paddingLeft: '12px' }}>
              "{doctor.bio || 'Bio not set.'}"
            </p>
          </div>
        </div>
      </div>

      {/* Appointment Queue */}
      <div className="glass-panel" style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
        <h2 style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <Calendar color="#aa3bff" /> Patients Scheduling Queue
        </h2>
        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>Patient</th>
                <th>Contact</th>
                <th>Emergency Contact</th>
                <th>Date</th>
                <th>Time</th>
                <th>Status</th>
                <th>Notes</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {appointments.map((app) => (
                <tr key={app.id}>
                  <td><strong>{app.patient.user.fullName}</strong></td>
                  <td>{app.patient.user.phone}</td>
                  <td>{app.patient.emergencyContact}</td>
                  <td>{app.appointmentDate}</td>
                  <td>{app.appointmentTime.substring(0, 5)}</td>
                  <td>
                    <span className={`badge badge-${app.status === 'CANCELLED' ? 'danger' : app.status === 'BOOKED' ? 'info' : 'success'}`}>
                      {app.status}
                    </span>
                  </td>
                  <td><span style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>{app.notes || '-'}</span></td>
                  <td>
                    {app.status === 'BOOKED' && (
                      <button onClick={() => handleCancelAppointment(app.id)} className="btn btn-danger" style={{ padding: '6px 12px', fontSize: '12px' }}>
                        Cancel
                      </button>
                    )}
                  </td>
                </tr>
              ))}
              {appointments.length === 0 && (
                <tr>
                  <td colSpan={8} style={{ textAlign: 'center', color: 'var(--text-secondary)' }}>No patient appointments scheduled.</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

// --- Admin Dashboard Component ---
function AdminDashboard() {
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAppointments();
  }, []);

  const fetchAppointments = async () => {
    try {
      const response = await api.get('/api/appointments/me'); // Admin context resolves all appointments
      setAppointments(response.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCancelAppointment = async (id: number) => {
    if (!confirm('Are you sure you want to cancel this appointment?')) return;
    try {
      await api.put(`/api/appointments/${id}/cancel`);
      fetchAppointments();
    } catch (err) {
      alert('Failed to cancel appointment');
    }
  };

  if (loading) {
    return (
      <div style={{ display: 'flex', flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <RefreshCw className="animate-spin" size={32} color="#aa3bff" />
      </div>
    );
  }

  return (
    <div style={{ maxWidth: '1200px', margin: '40px auto', padding: '0 24px', display: 'flex', flexDirection: 'column', gap: '30px' }}>
      
      <div className="glass-panel" style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
        <h2 style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <Shield color="#aa3bff" /> Global Administration Console
        </h2>
        <p style={{ color: 'var(--text-secondary)', fontSize: '15px' }}>
          Authorized system administrators can monitor all bookings, schedules, and active patient-doctor relationships across the platform.
        </p>

        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>Appointment ID</th>
                <th>Doctor</th>
                <th>Patient</th>
                <th>Date</th>
                <th>Time</th>
                <th>Status</th>
                <th>Notes</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {appointments.map((app) => (
                <tr key={app.id}>
                  <td><strong>#{app.id}</strong></td>
                  <td>{app.doctor.user.fullName}</td>
                  <td>{app.patient.user.fullName}</td>
                  <td>{app.appointmentDate}</td>
                  <td>{app.appointmentTime.substring(0, 5)}</td>
                  <td>
                    <span className={`badge badge-${app.status === 'CANCELLED' ? 'danger' : app.status === 'BOOKED' ? 'info' : 'success'}`}>
                      {app.status}
                    </span>
                  </td>
                  <td><span style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>{app.notes || '-'}</span></td>
                  <td>
                    {app.status === 'BOOKED' && (
                      <button onClick={() => handleCancelAppointment(app.id)} className="btn btn-danger" style={{ padding: '6px 12px', fontSize: '12px' }}>
                        Cancel
                      </button>
                    )}
                  </td>
                </tr>
              ))}
              {appointments.length === 0 && (
                <tr>
                  <td colSpan={8} style={{ textAlign: 'center', color: 'var(--text-secondary)' }}>No global appointments scheduled in the database.</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
