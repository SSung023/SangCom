export const allowedRole = {
    // no role
    '/': ['STUDENT', 'TEACHER', 'ADMIN', 'GRADE1', 'GRADE2', 'GRADE3'],
    council: ['STUDENT', 'TEACHER', 'ADMIN', 'GRADE1', 'GRADE2', 'GRADE3'],
    suggestion: ['STUDENT', 'TEACHER', 'ADMIN', 'GRADE1', 'GRADE2', 'GRADE3'],
    club: ['STUDENT', 'TEACHER', 'ADMIN', 'GRADE1', 'GRADE2', 'GRADE3'],
    '/timetable': ['STUDENT', 'TEACHER', 'ADMIN', 'GRADE1', 'GRADE2', 'GRADE3'],
    '/mealPage': ['STUDENT', 'TEACHER', 'ADMIN', 'GRADE1', 'GRADE2', 'GRADE3'],
    '/myarticle': ['STUDENT', 'TEACHER', 'ADMIN', 'GRADE1', 'GRADE2', 'GRADE3'],
    '/mycommentarticle': ['STUDENT', 'TEACHER', 'ADMIN', 'GRADE1', 'GRADE2', 'GRADE3'],
    '/my': ['STUDENT', 'TEACHER', 'ADMIN', 'GRADE1', 'GRADE2', 'GRADE3'],
    '/mynotif': ['STUDENT', 'TEACHER', 'ADMIN', 'GRADE1', 'GRADE2', 'GRADE3'],
    
    // restrict role
    free: ['STUDENT', 'ADMIN', 'GRADE1', 'GRADE2', 'GRADE3'],
    grade1: ['STUDENT', 'ADMIN', 'GRADE1'],
    grade2: ['STUDENT', 'ADMIN', 'GRADE2'],
    grade3: ['STUDENT', 'ADMIN', 'GRADE3'],
    '/myscrap': ['STUDENT', 'ADMIN', 'GRADE1', 'GRADE2', 'GRADE3']
};