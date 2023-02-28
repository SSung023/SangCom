export const allowedRole = {
    // no role
    '/': ['STUDENT', 'TEACHER', 'ADMIN'],
    council: ['STUDENT', 'TEACHER', 'ADMIN'],
    suggestion: ['STUDENT', 'TEACHER', 'ADMIN'],
    club: ['STUDENT', 'TEACHER', 'ADMIN'],
    '/timetable': ['STUDENT', 'TEACHER', 'ADMIN'],
    '/mealPage': ['STUDENT', 'TEACHER', 'ADMIN'],
    '/myarticle': ['STUDENT', 'TEACHER', 'ADMIN'],
    '/mycommentarticle': ['STUDENT', 'TEACHER', 'ADMIN'],
    '/my': ['STUDENT', 'TEACHER', 'ADMIN'],
    '/mynotif': ['STUDENT', 'TEACHER', 'ADMIN'],
    
    // restrict role
    free: ['STUDENT', 'ADMIN'],
    grade1: ['STUDENT', 'ADMIN'],
    grade2: ['STUDENT', 'ADMIN'],
    grade3: ['STUDENT', 'ADMIN'],
    '/myscrap': ['STUDENT', 'ADMIN']
};