package com.stud.backend.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "com.stud.backend",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class ModuleBoundaryTest {

    @ArchTest
    static final ArchRule api_packages_should_not_depend_on_own_domain_or_repository =
            noClasses()
                    .that().resideInAnyPackage(
                            "..profiles.api..",
                            "..users.api..",
                            "..dictionary.api.."
                    )
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..profiles.domain..",
                            "..profiles.repository..",
                            "..profiles.service..",
                            "..profiles.web..",
                            "..users.domain..",
                            "..users.repository..",
                            "..users.service..",
                            "..users.web..",
                            "..dictionary.domain..",
                            "..dictionary.repository..",
                            "..dictionary.service..",
                            "..dictionary.web.."
                    );

    @ArchTest
    static final ArchRule profiles_should_not_depend_on_other_module_internals =
            noClasses()
                    .that().resideInAPackage("..profiles..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..users.domain..",
                            "..users.repository..",
                            "..users.service..",
                            "..users.web..",
                            "..dictionary.domain..",
                            "..dictionary.repository..",
                            "..dictionary.service..",
                            "..dictionary.web.."
                    );

    @ArchTest
    static final ArchRule common_should_not_depend_on_feature_modules =
            noClasses()
                    .that().resideInAPackage("..common..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..profiles..",
                            "..users..",
                            "..dictionary..",
                            "..auth.."
                    );

    @ArchTest
    static final ArchRule users_should_not_depend_on_feature_modules =
            noClasses()
                    .that().resideInAPackage("..users..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..profiles..",
                            "..dictionary..",
                            "..auth.."
                    );

    @ArchTest
    static final ArchRule dictionary_should_not_depend_on_feature_modules =
            noClasses()
                    .that().resideInAPackage("..dictionary..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..profiles..",
                            "..users..",
                            "..auth.."
                    );

    @ArchTest
    static final ArchRule auth_should_not_depend_on_business_modules =
            noClasses()
                    .that().resideInAPackage("..auth..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..profiles..",
                            "..dictionary.."
                    );

}
