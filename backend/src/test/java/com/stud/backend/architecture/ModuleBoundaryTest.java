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
    static final ArchRule applications_should_not_depend_on_module_internals =
            noClasses()
                    .that().resideInAPackage("..applications..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..orders.domain..",
                            "..orders.repository..",
                            "..profiles.domain..",
                            "..profiles.repository..",
                            "..users.domain..",
                            "..users.repository..",
                            "..dictionary.domain..",
                            "..dictionary.repository.."
                    );

    @ArchTest
    static final ArchRule orders_should_not_depend_on_other_module_internals =
            noClasses()
                    .that().resideInAPackage("..orders..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..applications.domain..",
                            "..applications.repository..",
                            "..profiles.domain..",
                            "..profiles.repository..",
                            "..users.domain..",
                            "..users.repository..",
                            "..dictionary.domain..",
                            "..dictionary.repository.."
                    );

    @ArchTest
    static final ArchRule api_packages_should_not_depend_on_own_domain_or_repository =
            noClasses()
                    .that().resideInAnyPackage(
                            "..orders.api..",
                            "..profiles.api..",
                            "..users.api..",
                            "..dictionary.api..",
                            "..applications.api.."
                    )
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..orders.domain..",
                            "..orders.repository..",
                            "..profiles.domain..",
                            "..profiles.repository..",
                            "..users.domain..",
                            "..users.repository..",
                            "..dictionary.domain..",
                            "..dictionary.repository..",
                            "..applications.domain..",
                            "..applications.repository.."
                    );
    @ArchTest
    static final ArchRule profiles_should_not_depend_on_other_module_internals =
            noClasses()
                    .that().resideInAPackage("..profiles..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..orders.domain..",
                            "..orders.repository..",
                            "..applications.domain..",
                            "..applications.repository..",
                            "..users.domain..",
                            "..users.repository..",
                            "..dictionary.domain..",
                            "..dictionary.repository.."
                    );

    @ArchTest
    static final ArchRule common_should_not_depend_on_feature_modules =
            noClasses()
                    .that().resideInAPackage("..common..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..applications..",
                            "..orders..",
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
                            "..applications..",
                            "..orders..",
                            "..profiles..",
                            "..dictionary..",
                            "..auth.."
                    );

    @ArchTest
    static final ArchRule dictionary_should_not_depend_on_feature_modules =
            noClasses()
                    .that().resideInAPackage("..dictionary..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..applications..",
                            "..orders..",
                            "..profiles..",
                            "..users..",
                            "..auth.."
                    );

    @ArchTest
    static final ArchRule auth_should_not_depend_on_business_modules =
            noClasses()
                    .that().resideInAPackage("..auth..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "..applications..",
                            "..orders..",
                            "..profiles..",
                            "..dictionary.."
                    );

}