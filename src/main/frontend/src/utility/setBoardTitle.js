export const boardTitle = (category) => {
    switch (category) {
        case 'free':
            return '자유 게시판'
        case 'grade1':
            return '1학년 게시판'
        case 'grade2':
            return '2학년 게시판'
        case 'grade3':
            return '3학년 게시판'
        case 'council':
            return '학생회 공지 게시판'
        case 'suggestion':
            return '학생회 건의 게시판'
        case 'club':
            return '동아리 게시판'
        default:
            break;
    }
}