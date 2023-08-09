package side.mimi.mdd.utils;

import java.util.Random;

public class CombineRandomNickname {
	String[] adjectives = {
			"맑은", "행복한", "밝은", "아름다운", "신선한", "깨끗한",
			"따뜻한", "상냥한", "잘생긴", "착한", "성실한", "배부른",
			"신나는", "재미있는", "유용한", "유쾌한", "좋은", "활기찬",
			"확실한", "편안한", "귀여운", "정직한", "근엄한", "지혜로운",
			"고요한", "강한", "느린", "늦은", "잠자는", "미소지은",
			"창피한", "놀라운", "조용한", "빠른", "높은", "낮은",
			"즐거운", "행운의", "즐겁고", "빛나는", "부드러운", "무거운",
			"가벼운", "거친", "거대한", "작은", "잘빠진", "진지한",
			"멋진", "못된", "무서운", "완벽한", "성숙한", "신비로운",
			"자유로운", "날카로운", "지루한", "나쁜", "예쁜", "무심한",
			"미친", "멋없는", "비싼", "싸구려", "두려운", "가난한",
			"냉정한", "짜증나는", "즐겁고", "냉소적인", "뻔한", "불안한",
			"무뚝뚝한", "이상한", "낙천적인", "모험적인", "부끄러운", "쾌활한",
			"별로인", "거절하는", "곤란한", "서운한", "당당한", "불행한",
			"좋아하는", "기쁜", "힘든", "귀중한", "아쉬운", "질투하는",
			"독특한", "따분한", "달콤한", "민감한", "무례한", "잘난척하는",
			"열정적인", "냉정한", "두려운", "화난", "답답한", "모범적인",
			"최고의", "만족스러운", "당연한", "무지한", "빡친", "당당한",
			"자랑스러운", "배울만한", "센스있는", "예의바른", "직관적인", "지적인",
			"귀찮은", "바쁜", "예민한", "평화로운", "유능한", "날카로운",
			"둔한", "잘놀란", "창의적인", "끈기있는", "자주하는", "흥미로운",
			"기운찬", "무기력한", "신중한", "용기있는", "의심하는", "확실한",
			"독보적인", "탐욕스러운", "비판적인", "질투스러운", "안정된", "즐거운"
	};

	String[] nouns = {
			"사과", "바나나", "딸기", "포도", "수박", "참외",
			"감자", "당근", "양파", "마늘", "고구마", "콩",
			"오이", "피망", "브로콜리", "시금치", "토마토", "상추",
			"포도", "배", "감", "귤", "체리", "복숭아",
			"레몬", "메론", "자두", "파인애플", "밤", "깻잎",
			"배추", "브린지", "버섯", "대파", "당근", "무",
			"건포도", "건두", "허브", "뱀부추", "허브", "밀가루",
			"쌀가루", "고추가루", "설탕", "소금", "후추", "향신료",
			"소세지", "베이컨", "닭가슴살", "연어", "새우", "조개",
			"참치", "계란", "우유", "요구르트", "치즈", "버터",
			"식빵", "라면", "스파게티", "소스", "미역", "김",
			"돼지고기", "소고기", "닭고기", "오리고기", "양고기", "삼겹살",
			"고기", "생선", "방어", "가자미", "잉어", "오징어",
			"문어", "조개", "갑오징어", "조개", "굴", "새우",
			"전복", "대하", "게", "랍스타", "연어", "홍합",
			"바지락", "오이", "호박", "감자", "고추", "당근",
			"토마토", "양파", "양배추", "종이", "종이컵", "물병",
			"플라스틱", "카드", "카드게임", "지도", "필통", "연필",
			"공책", "손수건", "가방", "신발", "양말", "모자",
			"손목시계", "안경", "컴퓨터", "노트북", "스마트폰", "텔레비전",
			"카메라", "오디오", "스피커", "마이크", "키보드", "마우스",
			"책", "신문", "잡지", "티셔츠", "바지", "드레스",
			"모자", "컵", "접시", "쟁반", "포크", "칼",
			"숟가락", "젓가락", "그릇", "수저", "테이블", "의자"
	};

	public String getRandomNickname() {
		Random random = new Random();

		int adjIndex = random.nextInt(adjectives.length);
		int nounIndex = random.nextInt(nouns.length);

		return adjectives[adjIndex] + " " + nouns[nounIndex];
	}
}
