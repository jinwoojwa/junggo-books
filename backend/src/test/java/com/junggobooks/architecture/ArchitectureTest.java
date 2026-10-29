package com.junggobooks.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * 패키지 의존 규칙을 검사한다. 구조와 이유는 docs/adr/012-hexagonal-package-structure.md. 아직 클래스가 없는 패키지에서도 통과하도록
 * allowEmptyShould를 켠다.
 */
@AnalyzeClasses(packages = "com.junggobooks", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule 도메인은_애플리케이션_어댑터_스프링을_참조하지_않는다 =
            noClasses()
                    .that()
                    .resideInAPackage("..domain..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage("..application..", "..adapter..", "org.springframework..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule 애플리케이션은_어댑터를_참조하지_않는다 =
            noClasses()
                    .that()
                    .resideInAPackage("..application..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("..adapter..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule 인바운드_어댑터는_아웃바운드_어댑터를_참조하지_않는다 =
            noClasses()
                    .that()
                    .resideInAPackage("..adapter.in..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("..adapter.out..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule 아웃바운드_어댑터는_인바운드_어댑터를_참조하지_않는다 =
            noClasses()
                    .that()
                    .resideInAPackage("..adapter.out..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("..adapter.in..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule book은_price를_참조하지_않는다 =
            noClasses()
                    .that()
                    .resideInAPackage("com.junggobooks.book..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("com.junggobooks.price..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule price는_book의_유스케이스_포트와_도메인만_쓴다 =
            noClasses()
                    .that()
                    .resideInAPackage("com.junggobooks.price..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(
                            "com.junggobooks.book.adapter..",
                            "com.junggobooks.book.application.service..",
                            "com.junggobooks.book.application.port.out..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule common은_컨텍스트를_참조하지_않는다 =
            noClasses()
                    .that()
                    .resideInAPackage("com.junggobooks.common..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage("com.junggobooks.book..", "com.junggobooks.price..")
                    .allowEmptyShould(true);
}
