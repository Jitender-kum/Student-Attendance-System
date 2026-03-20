import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useStudentStore = defineStore('students', () => {
  const students = ref([
    { id: 1, firstName: 'Aarav',   lastName: 'Sharma',   roll: 'S001', classSection: '10-A', email: 'aarav.sharma@school.edu',   phone: '9876543210', gender: 'Male' },
    { id: 2, firstName: 'Priya',   lastName: 'Patel',    roll: 'S002', classSection: '10-A', email: 'priya.patel@school.edu',    phone: '9876543211', gender: 'Female' },
    { id: 3, firstName: 'Rohan',   lastName: 'Mehta',    roll: 'S003', classSection: '10-B', email: 'rohan.mehta@school.edu',    phone: '9876543212', gender: 'Male' },
    { id: 4, firstName: 'Sneha',   lastName: 'Verma',    roll: 'S004', classSection: '10-B', email: 'sneha.verma@school.edu',    phone: '9876543213', gender: 'Female' },
    { id: 5, firstName: 'Karan',   lastName: 'Gupta',    roll: 'S005', classSection: '11-A', email: 'karan.gupta@school.edu',    phone: '9876543214', gender: 'Male' },
    { id: 6, firstName: 'Anjali',  lastName: 'Singh',    roll: 'S006', classSection: '11-A', email: 'anjali.singh@school.edu',   phone: '9876543215', gender: 'Female' },
    { id: 7, firstName: 'Vikram',  lastName: 'Reddy',    roll: 'S007', classSection: '11-B', email: 'vikram.reddy@school.edu',   phone: '9876543216', gender: 'Male' },
    { id: 8, firstName: 'Neha',    lastName: 'Joshi',    roll: 'S008', classSection: '11-B', email: 'neha.joshi@school.edu',     phone: '9876543217', gender: 'Female' },
    { id: 9, firstName: 'Arjun',   lastName: 'Nair',     roll: 'S009', classSection: '12-A', email: 'arjun.nair@school.edu',     phone: '9876543218', gender: 'Male' },
    { id: 10,firstName: 'Pooja',   lastName: 'Iyer',     roll: 'S010', classSection: '12-A', email: 'pooja.iyer@school.edu',     phone: '9876543219', gender: 'Female' },
  ])

  let nextId = 11

  function addStudent(data) {
    students.value.push({ id: nextId++, ...data })
  }

  function updateStudent(id, data) {
    const idx = students.value.findIndex(s => s.id === id)
    if (idx !== -1) students.value[idx] = { id, ...data }
  }

  function deleteStudent(id) {
    students.value = students.value.filter(s => s.id !== id)
  }

  return { students, addStudent, updateStudent, deleteStudent }
})
