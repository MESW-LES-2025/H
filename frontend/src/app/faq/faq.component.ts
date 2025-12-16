import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

type FaqItem = { q: string; a: string };
type FaqGroup = { title: string; items: FaqItem[] };

@Component({
  selector: 'app-faq',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './faq.component.html',
  styleUrls: ['./faq.component.css'],
})
export class FaqComponent {
  query = '';
  activeGroupIndex = 0;

faqGroups: FaqGroup[] = [
  {
    title: 'Getting started',
    items: [
      {
        q: 'What is Lernia?',
        a: 'Lernia is a platform that helps you explore universities and courses, compare options, and save your favourites in one place.'
      },
      {
        q: 'Who is Lernia for?',
        a: 'Lernia is designed for students who are exploring higher education options and want a simple way to compare universities and courses.'
      },
      {
        q: 'Is Lernia free to use?',
        a: 'Yes. Browsing universities, courses, and using filters is completely free.'
      },
      {
        q: 'Do I need an account to explore?',
        a: 'No. You can explore freely without an account. Creating an account unlocks personalised features like favourites.'
      },
      {
        q: 'Which countries are supported?',
        a: 'Lernia currently focuses on a curated set of countries and institutions, and coverage is continuously expanding.'
      },
    ],
  },

  {
    title: 'Explore and filters',
    items: [
      {
        q: 'How does the Explore page work?',
        a: 'The Explore page lets you browse universities and courses using filters such as location, type, tuition range, and ranking.'
      },
      {
        q: 'What filters can I use?',
        a: 'You can filter by location, institution type, tuition fees, and ranking to narrow results based on your preferences.'
      },
      {
        q: 'Why do filters reset sometimes?',
        a: 'Filters may reset when you refresh the page or change major navigation sections.'
      },
      {
        q: 'Why are some universities missing information?',
        a: 'Some data depends on availability from official sources. We prioritise accuracy over completeness.'
      },
      {
        q: 'How often is the data updated?',
        a: 'University and course data is reviewed and updated regularly as new information becomes available.'
      },
    ],
  },

  {
    title: 'Courses',
    items: [
      {
        q: 'Can I explore courses without choosing a university?',
        a: 'Yes. You can browse courses independently and then see which universities offer them.'
      },
      {
        q: 'What kind of courses are listed?',
        a: 'Lernia focuses on higher education courses, including undergraduate and postgraduate programmes.'
      },
      {
        q: 'Are course details official?',
        a: 'Course descriptions are based on publicly available information and official university sources whenever possible.'
      },
      {
        q: 'Can I compare courses?',
        a: 'You can compare courses informally by saving them and reviewing their details side by side.'
      },
    ],
  },

  {
    title: 'Favourites',
    items: [
      {
        q: 'How do I save a university or course?',
        a: 'Click the heart icon on a card or on the detail page to add it to your favourites.'
      },
      {
        q: 'Where can I see my favourites?',
        a: 'Saved items appear in your profile under the favourites section.'
      },
      {
        q: 'Can I remove favourites?',
        a: 'Yes. Click the heart icon again to remove an item from your favourites.'
      },
      {
        q: 'Are favourites synced across devices?',
        a: 'Yes. As long as you are logged in, your favourites are saved to your account.'
      },
    ],
  },

  {
    title: 'Account and profile',
    items: [
      {
        q: 'How do I create an account?',
        a: 'You can register using the sign-up page by providing your basic information.'
      },
      {
        q: 'Can I edit my profile information?',
        a: 'Yes. You can update your profile details from your profile page at any time.'
      },
      {
        q: 'I forgot my password. What should I do?',
        a: 'Use the password reset option on the login page. You will receive instructions by email.'
      },
      {
        q: 'Can I delete my account?',
        a: 'Account deletion is currently not automated. Please contact support if you need assistance.'
      },
    ],
  },

  {
    title: 'Privacy and security',
    items: [
      {
        q: 'Is my data safe on Lernia?',
        a: 'Yes. We take data protection seriously and follow best practices to keep your information secure.'
      },
      {
        q: 'What personal data does Lernia store?',
        a: 'Only essential account information and your saved preferences are stored.'
      },
      {
        q: 'Is my data shared with third parties?',
        a: 'No. Your personal data is not sold or shared with external parties.'
      },
    ],
  },

  {
    title: 'Support and feedback',
    items: [
      {
        q: 'How can I contact support?',
        a: 'You can reach us through the Contact Support button or via the About page.'
      },
      {
        q: 'How long does support take to respond?',
        a: 'We aim to respond to all messages as quickly as possible.'
      },
      {
        q: 'Can I suggest new features?',
        a: 'Absolutely. We welcome feedback and feature suggestions from users.'
      },
      {
        q: 'How do I report a bug?',
        a: 'If you encounter an issue, please contact support and describe the problem in detail.'
      },
    ],
  },
];


  private openMap = new Map<string, number | null>();

  get filteredGroups(): FaqGroup[] {
    const q = this.query.trim().toLowerCase();
    if (!q) return this.faqGroups;

    const filtered = this.faqGroups
      .map((g) => ({
        ...g,
        items: g.items.filter((it) =>
          (it.q + ' ' + it.a).toLowerCase().includes(q),
        ),
      }))
      .filter((g) => g.items.length > 0);

    if (this.activeGroupIndex >= filtered.length) this.activeGroupIndex = 0;
    return filtered;
  }

  toggle(groupTitle: string, idx: number) {
    const current = this.openMap.get(groupTitle) ?? null;
    this.openMap.set(groupTitle, current === idx ? null : idx);
  }

  isOpen(groupTitle: string, idx: number) {
    return (this.openMap.get(groupTitle) ?? null) === idx;
  }

  onSearch() {
    this.activeGroupIndex = 0;

    const first = this.filteredGroups[0];
    if (!first) return;

    this.openMap.set(first.title, 0);
  }

  setQuery(q: string) {
    this.query = q;
    this.onSearch();
  }

  clearSearch() {
    this.query = '';
    this.activeGroupIndex = 0;
    this.openMap.clear();
  }

  contactSupport() {
    window.location.href = '/about';
  }
  
}
