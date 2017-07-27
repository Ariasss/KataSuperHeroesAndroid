/*
 * Copyright (C) 2015 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.karumi.katasuperheroes;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.karumi.katasuperheroes.di.MainComponent;
import com.karumi.katasuperheroes.di.MainModule;
import com.karumi.katasuperheroes.matchers.RecyclerViewItemsCountMatcher;
import com.karumi.katasuperheroes.model.SuperHero;
import com.karumi.katasuperheroes.model.SuperHeroesRepository;
import com.karumi.katasuperheroes.recyclerview.RecyclerViewInteraction;
import com.karumi.katasuperheroes.ui.view.MainActivity;
import it.cosenonjaviste.daggermock.DaggerMockRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static android.R.attr.name;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class) @LargeTest public class MainActivityTest {

  @Rule public DaggerMockRule<MainComponent> daggerRule =
      new DaggerMockRule<>(MainComponent.class, new MainModule()).set(
          new DaggerMockRule.ComponentSetter<MainComponent>() {
            @Override public void setComponent(MainComponent component) {
              SuperHeroesApplication app =
                  (SuperHeroesApplication) InstrumentationRegistry.getInstrumentation()
                      .getTargetContext()
                      .getApplicationContext();
              app.setComponent(component);
            }
          });

  @Rule public IntentsTestRule<MainActivity> activityRule =
      new IntentsTestRule<>(MainActivity.class, true, false);

  @Mock SuperHeroesRepository repository;

  @Test
  public void showsEmptyCaseIfThereAreNoSuperHeroes() {
    givenThereAreNoSuperHeroes();

    startActivity();

    onView(withText("¯\\_(ツ)_/¯")).check(matches(isDisplayed()));
  }

  @Test
  public void showsCaseIfThereAreSuperHeroes() {
    givenThereAreSuperHeroes();

    startActivity();

    onView(withText("¯\\_(ツ)_/¯")).check(matches(not(isDisplayed())));
  }

  @Test
  public void doesNotShowProgressBarIfThereAreSuperHeroes() {
    givenThereAreSuperHeroes();

    startActivity();

    onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())));
  }

  @Test
  public void checkTheSuperHeroesSentAreThere(){
    final List<SuperHero> superheroes = givenThereAreSuperHeroes();

    startActivity();

    final List<String> names = new ArrayList<>();
    for (SuperHero superHero : superheroes) {
      names.add(superHero.getName());
    }

    RecyclerViewInteraction.<String>onRecyclerView(withId(R.id.recycler_view))
    .withItems(names)
            .check(new RecyclerViewInteraction.ItemViewAssertion<String>() {
              @Override
              public void check(String name, View view, NoMatchingViewException e) {
                matches(hasDescendant(withText(name))).check(view, e);
              }
            });

  }
/*
  @Test
  public void doesShowProgressBarIfThereAreSuperHeroes() {
    givenThereAreSuperHeroes();

    startActivity();

    //onView(withId(R.id.recycler_view)).perform(click());
    onView(withId(R.id.progress_bar)).check(matches(isDisplayed()));
  }
*/
  private List<SuperHero> givenThereAreSuperHeroes() {
    /*List<SuperHero> nonEmptyList = new ArrayList<>();
    SuperHero hero = new SuperHero("Iron Man", "https://i.annihil.us/u/prod/marvel/i/mg/c/60/55b6a28ef24fa.jpg",
            true, "Wounded, captured and forced to build a weapon by his enemies, billionaire "
            + "industrialist Tony Stark instead created an advanced suit of armor to save his "
            + "life and escape captivity. Now with a new outlook on life, Tony uses his money "
            + "and intelligence to make the world a safer, better place as Iron Man.");
    nonEmptyList.add(hero);*/
    final List<SuperHero> superheroes = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      SuperHero superhero = new SuperHero("name " +i, null, false, "de");
      superheroes.add(superhero);
    }
    when(repository.getAll()).thenReturn(superheroes);
    return superheroes;
    /*when(repository.getAll()).then(new Answer<Object>() {
      @Override
      public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
        Thread.sleep(3333);
        return superheroes;
      }
    });*/
  }

  private void givenThereAreNoSuperHeroes() {
    List<SuperHero> emptyList = new ArrayList<>();
    when(repository.getAll()).thenReturn(emptyList);
  }

  private MainActivity startActivity() {
    return activityRule.launchActivity(null);
  }
}